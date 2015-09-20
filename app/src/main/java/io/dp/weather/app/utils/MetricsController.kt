package io.dp.weather.app.utils

import android.content.SharedPreferences

import javax.inject.Inject

import io.dp.weather.app.Const
import io.dp.weather.app.annotation.ConfigPrefs

/**
 * Created by dp on 11/10/14.
 */
public class MetricsController
@Inject
constructor(@ConfigPrefs private val prefs: SharedPreferences) {

    public fun useCelsius(): Boolean {
        return prefs.getBoolean(Const.USE_CELCIUS, false)
    }

    public fun useKmph(): Boolean {
        return prefs.getBoolean(Const.USE_KMPH, false)
    }

    public fun useMmhg(): Boolean {
        return prefs.getBoolean(Const.USE_MMHG, false)
    }
}
