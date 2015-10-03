package io.dp.weather.app.adapter

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.support.v4.app.FragmentActivity
import android.support.v4.util.LruCache
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.InjectView
import com.google.gson.Gson
import com.squareup.otto.Bus
import com.squareup.picasso.Picasso
import com.trello.rxlifecycle.components.ActivityLifecycleProvider
import io.dp.weather.app.Const
import io.dp.weather.app.R
import io.dp.weather.app.SchedulersManager
import io.dp.weather.app.annotation.CachePrefs
import io.dp.weather.app.annotation.PerActivity
import io.dp.weather.app.db.OrmliteCursorAdapter
import io.dp.weather.app.db.table.Place
import io.dp.weather.app.event.DeletePlaceEvent
import io.dp.weather.app.net.WeatherApi
import io.dp.weather.app.net.dto.Forecast
import io.dp.weather.app.utils.MetricsController
import io.dp.weather.app.utils.WhiteBorderCircleTransformation
import io.dp.weather.app.widget.WeatherFor5DaysView
import javax.inject.Inject

/**
 * Created by dp on 08/10/14.
 */
@PerActivity
public class PlacesAdapter
@Inject constructor(private val activity: FragmentActivity, private val gson: Gson, private val api: WeatherApi, private val bus: Bus) : OrmliteCursorAdapter<Place>(
        activity, null, null) {

    private val inflater: LayoutInflater
    private var prefs: SharedPreferences? = null

    private val cache = LruCache<Long, Forecast>(16)

    private var schedulersManager: SchedulersManager? = null
    private var metrics: MetricsController? = null

    private val transformation = WhiteBorderCircleTransformation()

    init {
        this.inflater = LayoutInflater.from(activity)
    }

    @Inject public fun setMetricsController(metrics: MetricsController) {
        this.metrics = metrics
    }

    @Inject public fun setSharedPreferences(@CachePrefs prefs: SharedPreferences) {
        this.prefs = prefs
    }

    @Inject public fun setSchedulersManager(schedulersManager: SchedulersManager) {
        this.schedulersManager = schedulersManager
    }

    public fun clear() {
        this.cache.evictAll()
        this.prefs!!.edit().clear().apply()
    }

    override fun newView(context: Context, cursor: Cursor, viewGroup: ViewGroup): View {
        val v = this.inflater.inflate(R.layout.item_city_weather, viewGroup, false)
        val holder = ViewHolder(v)
        v.tag = holder
        return v
    }

    override fun bindView(itemView: View, context: Context, item: Place?) {
        val holder = itemView.tag as ViewHolder

        val hash = "${item?.hashCode()}"
        holder.cityName.text = item?.name
        holder.temperatureView.text = ""

        holder.menuView.tag = item?.id
        holder.menuView.setOnClickListener(popupOnClickListener)

        if (metrics!!.useCelsius()) {
            holder.degreeTypeView.setText(R.string.celcius)
        } else {
            holder.degreeTypeView.setText(R.string.fahrenheit)
        }

        val lastRequestTime = prefs!!.getLong(hash + "_time", -1)
        if (lastRequestTime.equals(-1) ||
            (lastRequestTime > 0 && (System.currentTimeMillis() - lastRequestTime) > DateUtils.DAY_IN_MILLIS)) {

            holder.progressView.visibility = View.VISIBLE
            holder.contentView.visibility = View.GONE

            val lat = item?.lat
            val lon = item?.lon

            val provider = activity as ActivityLifecycleProvider

            val query = "$lat,$lon"
            api.getForecast(query, Const.FORECAST_FOR_DAYS)
                    .compose(schedulersManager!!.applySchedulers<Any>(provider))
                    .subscribe({ forecast -> run {
                        val now = System.currentTimeMillis()
                        prefs!!.edit().putLong(hash + "_time", now).apply()
                        prefs!!.edit().putString(hash, gson.toJson(forecast)).apply()
                        notifyDataSetChanged()
                    } })
        } else {
            holder.progressView.visibility = View.GONE
            holder.contentView.visibility = View.VISIBLE

            var f = cache.get(item?.id)
            if (f == null) {
                // forecast exists - load it from cache
                val rawForecast = prefs!!.getString(hash, null)
                f = gson.fromJson(rawForecast, Forecast::class.java)
                cache.put(item?.id, f)
            }

            val conditions = f!!.data?.currentCondition
            if (conditions != null && conditions.size() > 0) {
                val condition = conditions.get(0)

                holder.humidityView.text = condition.humidity + "%"

                try {
                    val pressure = Integer.valueOf(condition.pressure)!!

                    if (metrics!!.useMmhg()) {
                        holder.pressureView.text = context.getString(R.string.fmt_pressure_mmhg,
                                                                     (pressure * Const.CONVERT_MMHG).toInt())
                    } else {
                        holder.pressureView.text = context.getString(R.string.fmt_pressure_kpa,
                                                                     pressure)
                    }
                } catch (e: NumberFormatException) {
                    holder.pressureView.setText(R.string.undef)
                }

                val sb = StringBuilder()
                sb.append(condition.winddir16Point).append(", ")
                if (metrics!!.useKmph()) {
                    sb.append(
                            context.getString(R.string.fmt_windspeed_kmph, condition.windspeedKmph))
                } else {
                    sb.append(
                            context.getString(R.string.fmt_windspeed_mph, condition.windspeedMiles))
                }

                holder.windView.text = sb.toString()

                val descList = condition.weatherDesc
                if (descList != null && descList.size() > 0) {
                    val description = descList.get(0).value
                    holder.weatherDescView.text = description
                }

                if (metrics!!.useCelsius()) {
                    holder.temperatureView.text = condition.tempC
                } else {
                    holder.temperatureView.text = condition.tempF
                }

                val urls = conditions.get(0).weatherIconUrl

                if (urls != null && urls.size() > 0) {
                    val url = urls.get(0).value
                    Picasso.with(activity).load(
                            if (!TextUtils.isEmpty(url)) url else null).transform(
                            transformation).into(holder.weatherState)
                }
            }

            holder.weatherFor5DaysView.setWeatherForWeek(f.data!!.weather, metrics!!.useCelsius(),
                                                         transformation)
        }
    }

    var popupOnClickListener: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View) {
            val id = v.tag as Long

            val popupMenu = PopupMenu(activity, v)
            popupMenu.inflate(R.menu.item_place)

            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    bus.post(DeletePlaceEvent(id))
                    return true
                }
            })

            popupMenu.show()
        }
    }

    class ViewHolder(view: View) {

        @InjectView(R.id.weather_state) lateinit var weatherState: ImageView
        @InjectView(R.id.city_name) lateinit var cityName: TextView
        @InjectView(R.id.weather_for_week) lateinit var weatherFor5DaysView: WeatherFor5DaysView
        @InjectView(R.id.temperature) lateinit var temperatureView: TextView
        @InjectView(R.id.degrees_type) lateinit var degreeTypeView: TextView
        @InjectView(R.id.weather_description) lateinit var weatherDescView: TextView
        @InjectView(R.id.progress) lateinit var progressView: ProgressBar
        @InjectView(R.id.content) lateinit var contentView: View
        @InjectView(R.id.menu) lateinit var menuView: View
        @InjectView(R.id.humidity) lateinit var humidityView: TextView
        @InjectView(R.id.pressure) lateinit var pressureView: TextView
        @InjectView(R.id.wind) lateinit var windView: TextView

        init {
            ButterKnife.inject(this, view)
        }
    }
}
