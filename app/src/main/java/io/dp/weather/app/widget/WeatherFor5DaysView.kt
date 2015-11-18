package io.dp.weather.app.widget

import android.content.Context
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import io.dp.weather.app.R
import io.dp.weather.app.net.dto.Weather
import kotlinx.android.synthetic.view_weather_for_week.view.*
import java.sql.Date

public class WeatherFor5DaysView : LinearLayout {

    lateinit var dayNameViews: List<TextView>
    lateinit var dayViews: List<ImageView>
    lateinit var tempViews: List<TextView>

    lateinit var transformation: Transformation

    var celsius: String? = null
    var fahrenheit: String? = null

    public constructor(context: Context) : super(context) {
        init(context)
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_weather_for_week, this, true)

        celsius = context.getString(R.string.celcius)
        fahrenheit = context.getString(R.string.fahrenheit)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        dayNameViews = arrayListOf(day_name_1, day_name_2, day_name_3, day_name_4, day_name_5)
        dayViews = arrayListOf(day_1, day_2, day_3, day_4, day_5)
        tempViews = arrayListOf(temp_1, temp_2, temp_3, temp_4, temp_5)
    }

    public fun setWeatherForWeek(weatherList: List<Weather>, useCelsius: Boolean, transformation: Transformation) {
        this.transformation = transformation

        for (i in weatherList.indices) {
            val v = dayViews[i]
            val weather = weatherList[i]

            try {
                val date = Date.valueOf(weather.date)
                val weekDay = DateUtils.formatDateTime(context, date.time, DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_WEEKDAY)
                dayNameViews[i].text = weekDay
            } catch (e: IllegalArgumentException) {
                dayNameViews[i].text = ""
            }

            if (useCelsius) {
                tempViews[i].text = "${weather.tempMinC}-${weather.tempMaxC}${context!!.getString(R.string.celcius)}"
            } else {
                tempViews[i].text = "${weather.tempMinF}-${weather.tempMaxF}${context!!.getString(R.string.fahrenheit)}"
            }

            val urls = weather.weatherIconUrl
            if (urls?.isNotEmpty() ?: false) {
                val url = urls?.get(0)
                if (!TextUtils.isEmpty(url?.value)) {
                    Picasso.with(context).load(url?.value).transform(transformation).into(v)
                }
            }
        }
    }
}
