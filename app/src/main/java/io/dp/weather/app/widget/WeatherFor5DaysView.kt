package io.dp.weather.app.widget

import android.content.Context
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.InjectViews
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import io.dp.weather.app.R
import io.dp.weather.app.net.dto.Weather
import java.sql.Date

/**
 * Created by dp on 08/10/14.
 */

public class WeatherFor5DaysView : LinearLayout {

    @InjectViews(R.id.day_name_1, R.id.day_name_2, R.id.day_name_3, R.id.day_name_4, R.id.day_name_5)
    lateinit var dayNameViews: Array<TextView>

    @InjectViews(R.id.day_1, R.id.day_2, R.id.day_3, R.id.day_4, R.id.day_5)
    lateinit var dayViews: Array<ImageView>

    @InjectViews(R.id.temp_1, R.id.temp_2, R.id.temp_3, R.id.temp_4, R.id.temp_5)
    lateinit var tempViews: Array<TextView>

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

    public fun setWeatherForWeek(weatherList: List<Weather>, useCelsius: Boolean, transformation: Transformation) {
        this.transformation = transformation

        for (i in weatherList.indices) {
            val v = dayViews[i]
            val weather = weatherList.get(i)

            try {
                val date = Date.valueOf(weather.date)
                val weekDay = DateUtils.formatDateTime(getContext(), date.time, DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_WEEKDAY)
                dayNameViews[i].text = weekDay
            } catch (e: IllegalArgumentException) {
                dayNameViews[i].text = ""
            }

            if (useCelsius) {
                tempViews[i].setText("${weather.tempMinC}-${weather.tempMaxC}${context!!.getString(R.string.celcius)}")
            } else {
                tempViews[i].setText("${weather.tempMinF}-${weather.tempMaxF}${context!!.getString(R.string.fahrenheit)}")
            }

            val urls = weather.weatherIconUrl
            if (urls != null && urls.size() > 0) {
                val url = urls.get(0)
                if (!TextUtils.isEmpty(url.value)) {
                    Picasso.with(getContext()).load(url.value).transform(transformation).into(v)
                }
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        ButterKnife.inject(this)
    }

}
