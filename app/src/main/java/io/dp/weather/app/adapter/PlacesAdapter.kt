package io.dp.weather.app.adapter

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.FragmentActivity
import android.support.v4.util.LruCache
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import io.dp.weather.app.utils.edit
import kotlinx.android.synthetic.item_city_weather.view.city_name
import kotlinx.android.synthetic.item_city_weather.view.content
import kotlinx.android.synthetic.item_city_weather.view.menu
import kotlinx.android.synthetic.item_city_weather.view.progress
import kotlinx.android.synthetic.item_place_content.view.*
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
            with (itemView) {
                city_name.text = place.name
                temperature.text = ""

                menu.tag = place.id
                menu.setOnClickListener(popupOnClickListener)

                when {
                    metrics.useCelsius -> degrees_type.setText(R.string.celcius)
                    else -> degrees_type.setText(R.string.fahrenheit)
                }

                progress.visibility = if (isNeedToUpdate) View.VISIBLE else View.GONE
                content.visibility = if (isNeedToUpdate) View.GONE else View.VISIBLE
            }
        }

        if (isNeedToUpdate) {
            api.getForecast("${place.lat},${place.lon}", Const.FORECAST_FOR_DAYS)
                    .compose(schedulersManager.applySchedulers<Forecast>())
                    .subscribeWith {
                        onNext {
                            prefs.edit {
                                arrayOf(hash + "_time" to System.currentTimeMillis(),
                                hash to gson.toJson(it))
                            }
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

        fun fillViewWithForecast(context: Context, forecast: Forecast, metrics: MetricsController, transformation: WhiteBorderCircleTransformation) {
            val conditions = forecast.data?.currentCondition
            if (conditions?.isNotEmpty() ?: false) {
                val condition = conditions?.get(0)

                with (itemView) {
                    humidity.text = "${condition?.humidity} %"

                    try {
                        val pressure_val = Integer.valueOf(condition?.pressure)!!

                        pressure.text = when {
                            metrics.useMmhg -> context.getString(R.string.fmt_pressure_mmhg, (pressure_val * Const.CONVERT_MMHG).toInt())
                            else -> context.getString(R.string.fmt_pressure_kpa, pressure_val)
                        }
                    } catch (e: NumberFormatException) {
                        pressure.setText(R.string.undef)
                    }

                    val metric = when {
                        metrics.useKmph -> context.getString(R.string.fmt_windspeed_kmph, condition?.windspeedKmph ?: "")
                        else -> context.getString(R.string.fmt_windspeed_mph, condition?.windspeedMiles ?: "")
                    }

                    wind.text = "${condition?.winddir16Point}, $metric"

                    val descList = condition?.weatherDesc
                    if (descList?.isNotEmpty() ?: false) {
                        val description = descList?.get(0)?.value ?: ""
                        weather_description.text = description
                    }

                    when {
                        metrics.useCelsius -> temperature.text = condition?.tempC ?: ""
                        else -> temperature.text = condition?.tempF ?: ""
                    }

                    val urls = conditions?.get(0)?.weatherIconUrl

                    if (urls?.isNotEmpty() ?: false) {
                        val url = urls?.get(0)?.value ?: ""
                        Picasso.with(context)
                                .load(url)
                                .transform(transformation)
                                .into(weather_state)
                    }
                }

            }

            val weather5days = forecast.data?.weather ?: listOf<Weather>()
            itemView.weather_for_week.setWeatherForWeek(weather5days, metrics.useCelsius, transformation)
        }
    }
}
