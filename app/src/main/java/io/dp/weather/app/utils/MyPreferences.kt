package io.dp.weather.app.utils

import android.content.SharedPreferences

/**
 * Created by dp on 15/11/15.
 */

fun SharedPreferences.myEdit(func: SharedPreferences.Editor.() -> Array<Pair<String, Any>>) : SharedPreferences.Editor {
    val editor = edit()

    val pairs = editor.func()

    for ((key, value) in pairs) {
        when (value) {
            is String -> editor.putString(key, value)
            is Set<*> -> {
                if (!value.all { it is String }) {
                    throw IllegalArgumentException("Only Set<String> is supported")
                }
                editor.putStringSet(key, value as Set<String>)
            }
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> throw IllegalArgumentException("Unsupported value type: ${value.javaClass}")
        }
    }

    if (pairs.size > 0) {
        editor.apply()
    }

    return editor
}
