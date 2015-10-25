package io.dp.weather.app.utils

import android.content.SharedPreferences
import io.dp.weather.app.Const
import io.dp.weather.app.annotation.ConfigPrefs
import javax.inject.Inject

/**
 * Created by dp on 11/10/14.
 */
public class MetricsController
@Inject
constructor(@ConfigPrefs private val prefs: SharedPreferences) {
    val useCelsius: Boolean
        get() = prefs.getBoolean(Const.USE_CELCIUS, false)

    val useKmph: Boolean
        get() = prefs.getBoolean(Const.USE_KMPH, false)

    val useMmhg: Boolean
        get() = prefs.getBoolean(Const.USE_MMHG, false)
}
