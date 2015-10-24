package io.dp.weather.app.fragment

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import butterknife.ButterKnife
import butterknife.InjectView
import com.etsy.android.grid.StaggeredGridView
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import com.trello.rxlifecycle.components.ActivityLifecycleProvider
import io.dp.weather.app.BusSubcomponent
import io.dp.weather.app.R
import io.dp.weather.app.SchedulersManager
import io.dp.weather.app.activity.SettingsActivity
import io.dp.weather.app.activity.debug.DebugActivity
import io.dp.weather.app.adapter.PlacesAdapter
import io.dp.weather.app.adapter.PlacesAutoCompleteAdapter
import io.dp.weather.app.db.DatabaseHelper
import io.dp.weather.app.db.OrmliteCursorLoader
import io.dp.weather.app.db.Queries
import io.dp.weather.app.db.table.Place
import io.dp.weather.app.event.AddPlaceEvent
import io.dp.weather.app.event.DeletePlaceEvent
import io.dp.weather.app.event.UpdateListEvent
import io.dp.weather.app.utils.Observables
import rx.Observer
import rx.Subscription
import java.sql.SQLException
import java.util.*
import javax.inject.Inject

public class WeatherFragment : BaseFragment(), LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener, Observer<Place> {

    var subscriptionList: MutableList<Subscription> = ArrayList()

    @Inject lateinit var geocoder: Geocoder
    @Inject lateinit var adapter: PlacesAdapter
    @Inject lateinit var dbHelper: DatabaseHelper
    @Inject lateinit var bus: Bus
    @Inject lateinit var placesAutoCompleteAdapter: PlacesAutoCompleteAdapter
    @Inject lateinit var schedulersManager: SchedulersManager

    @InjectView(R.id.grid) lateinit var gridView: StaggeredGridView
    @InjectView(R.id.swipe_layout) lateinit var swipeRefreshView: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater!!.inflate(R.layout.fragment_weather, container, false)
        ButterKnife.inject(this, v)

        swipeRefreshView.setOnRefreshListener(this)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (component as BusSubcomponent).inject(this)

        retainInstance = true

        adapter.query = Queries.prepareCityQuery(dbHelper)
        gridView.adapter = adapter

        loaderManager.restartLoader(0, null, this)

        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        bus.register(this)

        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        bus.unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater!!.inflate(R.menu.main, menu)

        val addItem = menu!!.findItem(R.id.action_add)

        val addView = addItem.actionView as AutoCompleteTextView
        MenuItemCompat.setOnActionExpandListener(addItem, object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                addView.post {
                    addView.requestFocus()
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(addView, InputMethodManager.SHOW_IMPLICIT)
                }
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        when (id) {
            R.id.action_add -> return true

            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }

            R.id.action_debug -> {
                startActivity(Intent(activity, DebugActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        for (s in subscriptionList) {
            s.unsubscribe()
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor>? {
        try {
            return OrmliteCursorLoader(activity, dbHelper.getPlaceDao(), adapter.query)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        adapter.changeCursor(cursor)
        gridView.post { gridView.setSelection(gridView.count - 1) }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.changeCursor(null)
    }

    override fun onRefresh() {
        swipeRefreshView.isRefreshing = true
        adapter.clear()
        adapter.notifyDataSetChanged()
        swipeRefreshView.isRefreshing = false
    }

    @Subscribe public fun onUpdateList(event: UpdateListEvent) {
        loaderManager.restartLoader(0, null, this)
    }

    @Subscribe public fun onDeletePlace(event: DeletePlaceEvent) {
        if (event.id != null) {
            try {
                dbHelper.getPlaceDao()!!.deleteById(event.id)
                loaderManager.restartLoader(0, null, this)
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }
    }

    @Subscribe public fun onAddPlace(event: AddPlaceEvent) {
        val s = Observables.getGeoForPlace(activity, dbHelper, geocoder, event.lookupPlace).compose(schedulersManager.applySchedulers<Place>(activity as ActivityLifecycleProvider)).subscribe(this@WeatherFragment)

        subscriptionList.add(s)
    }

    override fun onCompleted() {

    }

    override fun onError(e: Throwable) {

    }

    override fun onNext(place: Place) {
        bus.post(UpdateListEvent())
    }

    companion object {

        public fun newInstance(): WeatherFragment {
            return WeatherFragment()
        }
    }
}
