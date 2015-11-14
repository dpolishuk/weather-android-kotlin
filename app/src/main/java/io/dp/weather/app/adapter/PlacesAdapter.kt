package io.dp.weather.app.adapter

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.FragmentActivity
import android.support.v4.util.LruCache
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.bindView
import com.google.gson.Gson
import com.squareup.otto.Bus
import com.squareup.picasso.Picasso
import io.dp.weather.app.Const
import io.dp.weather.app.R
import io.dp.weather.app.SchedulersManager
import io.dp.weather.app.annotation.CachePrefs
import io.dp.weather.app.annotation.PerActivity
import io.dp.weather.app.db.table.Place
import io.dp.weather.app.event.DeletePlaceEvent
import io.dp.weather.app.net.WeatherApi
import io.dp.weather.app.net.dto.Forecast
import io.dp.weather.app.net.dto.Weather
import io.dp.weather.app.utils.MetricsController
import io.dp.weather.app.utils.WhiteBorderCircleTransformation
import io.dp.weather.app.widget.WeatherFor5DaysView
import rx.lang.kotlin.subscribeWith
import timber.log.Timber
import javax.inject.Inject

@PerActivity
class PlacesAdapter
@Inject constructor(val activity: FragmentActivity,
                    val gson: Gson,
                    val api: WeatherApi,
                    val bus: Bus) : OrmliteCursorRecyclerViewAdapter<Place, PlacesAdapter.Holder>() {

    private val cache = LruCache<Long, Forecast>(16)

    private lateinit var schedulersManager: SchedulersManager
    private lateinit var prefs: SharedPreferences
    private lateinit var metrics: MetricsController

    private val transformation = WhiteBorderCircleTransformation()

    @Inject
    fun setMetricsController(metrics: MetricsController) {
        this.metrics = metrics
    }

    @Inject
    fun setSharedPreferences(@CachePrefs prefs: SharedPreferences) {
        this.prefs = prefs
    }

    @Inject
    fun setSchedulersManager(schedulersManager: SchedulersManager) {
        this.schedulersManager = schedulersManager
    }

    public fun clear() {
        this.cache.evictAll()
        this.prefs.edit().clear().apply()
    }

    fun SharedPreferences.isNeedToUpdateWeather(hash: String): Boolean {
        val lastRequestTime = prefs.getLong(hash + "_time", -1)
        return lastRequestTime == -1L
                || (lastRequestTime > 0
                && (System.currentTimeMillis() - lastRequestTime) > DateUtils.DAY_IN_MILLIS)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder? {
        return Holder(activity.layoutInflater.inflate(R.layout.item_city_weather, parent, false))
    }

    fun getForecast(place: Place): Forecast? {
        val hash = "${place.hashCode()}"
        var forecast = cache.get(place.id)
        if (forecast == null && place.id != null) {
            // forecast exists - load it from cache
            val rawForecast = prefs.getString(hash, null)
            forecast = gson.fromJson(rawForecast, Forecast::class.java)
            if (forecast != null) {
                cache.put(place.id, forecast)
            }
        }

        return forecast
    }

    override fun onBindViewHolder(holder: Holder, place: Place) {
        val hash = "${place.hashCode()}"

        val isNeedToUpdate = prefs.isNeedToUpdateWeather(hash)

        with (holder) {
            cityName.text = place.name
            temperatureView.text = ""

            menuView.tag = place.id
            menuView.setOnClickListener(popupOnClickListener)

            when {
                metrics.useCelsius -> degreeTypeView.setText(R.string.celcius)
                else -> degreeTypeView.setText(R.string.fahrenheit)
            }
            progressView.visibility = if (isNeedToUpdate) View.VISIBLE else View.GONE
            contentView.visibility = if (isNeedToUpdate) View.GONE else View.VISIBLE
        }

        if (isNeedToUpdate) {
            api.getForecast("${place.lat},${place.lon}", Const.FORECAST_FOR_DAYS)
                    .compose(schedulersManager.applySchedulers<Forecast>())
                    .subscribeWith {
                        onNext {
                            prefs.edit().putLong(hash + "_time", System.currentTimeMillis()).apply()
                            prefs.edit().putString(hash, gson.toJson(it)).apply()
                            notifyDataSetChanged()
                        }

                        onError {
                            Timber.e(it, "Got throwable")
                        }
                    }
        } else {
            val forecast = getForecast(place) ?: Forecast()
            holder.fillViewWithForecast(activity, forecast, metrics, transformation)
        }
    }

    val popupOnClickListener: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View) {
            val id = v.tag as Long

            val popupMenu = PopupMenu(activity, v)

            with (popupMenu) {
                inflate(R.menu.item_place)

                setOnMenuItemClickListener {
                    bus.post(DeletePlaceEvent(id))
                    true
                }

                show()
            }

        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weatherState: ImageView by bindView(R.id.weather_state)
        val cityName: TextView by bindView(R.id.city_name)
        val weatherFor5DaysView: WeatherFor5DaysView by bindView(R.id.weather_for_week)
        val temperatureView: TextView by bindView(R.id.temperature)
        val degreeTypeView: TextView by bindView(R.id.degrees_type)
        val weatherDescView: TextView by bindView(R.id.weather_description)
        val progressView: ProgressBar by bindView(R.id.progress)
        val contentView: View by bindView(R.id.content)
        val menuView: View by bindView(R.id.menu)
        val humidityView: TextView by bindView(R.id.humidity)
        val pressureView: TextView by bindView(R.id.pressure)
        val windView: TextView by bindView(R.id.wind)

        fun fillViewWithForecast(context: Context, forecast: Forecast, metrics: MetricsController, transformation: WhiteBorderCircleTransformation) {
            val conditions = forecast.data?.currentCondition
            if (conditions?.isNotEmpty() ?: false) {
                val condition = conditions?.get(0)

                humidityView.text = "${condition?.humidity} %"

                try {
                    val pressure = Integer.valueOf(condition?.pressure)!!

                    pressureView.text = when {
                        metrics.useMmhg -> context.getString(R.string.fmt_pressure_mmhg, (pressure * Const.CONVERT_MMHG).toInt())
                        else -> context.getString(R.string.fmt_pressure_kpa, pressure)
                    }
                } catch (e: NumberFormatException) {
                    pressureView.setText(R.string.undef)
                }

                val metric = when {
                    metrics.useKmph -> context.getString(R.string.fmt_windspeed_kmph, condition?.windspeedKmph ?: "")
                    else -> context.getString(R.string.fmt_windspeed_mph, condition?.windspeedMiles ?: "")
                }

                windView.text = "${condition?.winddir16Point}, $metric"

                val descList = condition?.weatherDesc
                if (descList?.isNotEmpty() ?: false) {
                    val description = descList?.get(0)?.value ?: ""
                    weatherDescView.text = description
                }

                when {
                    metrics.useCelsius -> temperatureView.text = condition?.tempC ?: ""
                    else -> temperatureView.text = condition?.tempF ?: ""
                }

                val urls = conditions?.get(0)?.weatherIconUrl

                if (urls?.isNotEmpty() ?: false) {
                    val url = urls?.get(0)?.value ?: ""
                    Picasso.with(context)
                            .load(url)
                            .transform(transformation)
                            .into(weatherState)
                }
            }

            val weather5days = forecast.data?.weather ?: listOf<Weather>()
            weatherFor5DaysView.setWeatherForWeek(weather5days, metrics.useCelsius, transformation)
        }
    }
}
