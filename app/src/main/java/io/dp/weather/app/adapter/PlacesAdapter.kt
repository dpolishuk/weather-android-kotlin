package io.dp.weather.app.adapter

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.support.v4.app.FragmentActivity
import android.support.v4.util.LruCache
import android.text.format.DateUtils
import android.view.LayoutInflater
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
import io.dp.weather.app.net.dto.Weather
import io.dp.weather.app.utils.MetricsController
import io.dp.weather.app.utils.WhiteBorderCircleTransformation
import io.dp.weather.app.widget.WeatherFor5DaysView
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by dp on 08/10/14.
 */
@PerActivity
public class PlacesAdapter
@Inject constructor(private val activity: FragmentActivity, private val gson: Gson, private val api: WeatherApi, private val bus: Bus) : OrmliteCursorAdapter<Place>(activity, null, null) {

  private val inflater: LayoutInflater
  private var prefs: SharedPreferences? = null

  private val cache = LruCache<Long, Forecast>(16)

  private var schedulersManager: SchedulersManager? = null
  private var metrics: MetricsController? = null

  private val transformation = WhiteBorderCircleTransformation()

  //  inner class ForecastCacheSubscriber(private val hash: String) : Subscriber<Forecast>() {
  //
  //    override public fun onCompleted() {
  //    }
  //
  //    override public fun onError(e: Throwable) {
  //    }
  //
  //    override public fun onNext(forecast: Forecast) {
  //      prefs!!.edit().putLong(hash + "_time", System.currentTimeMillis()).apply()
  //      prefs!!.edit().putString(hash, gson.toJson(forecast)).apply()
  //      notifyDataSetChanged()
  //    }
  //  }

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

  fun SharedPreferences.isNeedToUpdateWeather(hash: String): Boolean {
    val lastRequestTime = prefs!!.getLong(hash + "_time", -1)
    return lastRequestTime == -1L
        || (lastRequestTime > 0
        && (System.currentTimeMillis() - lastRequestTime) > DateUtils.DAY_IN_MILLIS)
  }

  override fun bindView(itemView: View, context: Context, place: Place?) {
    val holder = itemView.tag as ViewHolder

    val hash = "${place?.hashCode()}"
    holder.cityName.text = place?.name
    holder.temperatureView.text = ""

    holder.menuView.tag = place?.id
    holder.menuView.setOnClickListener(popupOnClickListener)

    when {
      metrics!!.useCelsius -> holder.degreeTypeView.setText(R.string.celcius)
      else -> holder.degreeTypeView.setText(R.string.fahrenheit)
    }

    if (prefs?.isNeedToUpdateWeather(hash) ?: true) {

      holder.progressView.visibility = View.VISIBLE
      holder.contentView.visibility = View.GONE

      val lat = place?.lat
      val lon = place?.lon

      val provider = activity as ActivityLifecycleProvider

      val query = "$lat,$lon"

      api.getForecast(query, Const.FORECAST_FOR_DAYS)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object: Subscriber<Forecast>() {

            override fun onNext(forecast: Forecast?) {
              prefs!!.edit().putLong(hash + "_time", System.currentTimeMillis()).apply()
              prefs!!.edit().putString(hash, gson.toJson(forecast)).apply()
              notifyDataSetChanged()
            }

            override fun onCompleted() {
            }

            override fun onError(e: Throwable?) {
              error("Got error: $e")
            }

          })
    } else {
      holder.progressView.visibility = View.GONE
      holder.contentView.visibility = View.VISIBLE

      val forecast = cache.get(place?.id)
      if (forecast == null && place?.id != null) {
        // forecast exists - load it from cache
        val rawForecast = prefs!!.getString(hash, null)
        val f = gson.fromJson(rawForecast, Forecast::class.java)
        if (f != null) {
          cache.put(place?.id, f)
        }
      }

      val conditions = forecast?.data?.currentCondition
      if (conditions?.isNotEmpty() ?: false) {
        val condition = conditions?.get(0)

        holder.humidityView.setText("${condition?.humidity} %")

        try {
          val pressure = Integer.valueOf(condition?.pressure)!!

          holder.pressureView.text = when {
            metrics!!.useMmhg -> context.getString(R.string.fmt_pressure_mmhg, (pressure * Const.CONVERT_MMHG).toInt())
            else -> context.getString(R.string.fmt_pressure_kpa, pressure)
          }
        } catch (e: NumberFormatException) {
          holder.pressureView.setText(R.string.undef)
        }

        val metric = when {
          metrics!!.useKmph -> context.getString(R.string.fmt_windspeed_kmph, condition?.windspeedKmph ?: "")
          else -> context.getString(R.string.fmt_windspeed_mph, condition?.windspeedMiles ?: "")
        }

        holder.windView.text = "${condition?.winddir16Point}, $metric"

        val descList = condition?.weatherDesc
        if (descList?.isNotEmpty() ?: false) {
          val description = descList?.get(0)?.value ?: ""
          holder.weatherDescView.text = description
        }

        when {
          metrics!!.useCelsius -> holder.temperatureView.text = condition?.tempC ?: ""
          else -> holder.temperatureView.text = condition?.tempF ?: ""
        }

        val urls = conditions?.get(0)?.weatherIconUrl

        if (urls?.isNotEmpty() ?: false) {
          val url = urls?.get(0)?.value ?: ""
          Picasso.with(activity)
              .load(url)
              .transform(transformation)
              .into(holder.weatherState)
        }
      }

      val weather5days = forecast?.data?.weather ?: listOf<Weather>()
      val isCelsius = metrics?.useCelsius ?: true

      holder.weatherFor5DaysView.setWeatherForWeek(weather5days, isCelsius, transformation)
    }
  }

  val popupOnClickListener: View.OnClickListener = object : View.OnClickListener {
    override fun onClick(v: View) {
      val id = v.tag as Long

      val popupMenu = PopupMenu(activity, v)
      popupMenu.inflate(R.menu.item_place)

      with (popupMenu) {
        onMenuItemClick {
          bus.post(DeletePlaceEvent(id))
          true
        }
      }

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
