package io.dp.weather.app.net.dto

import com.google.gson.annotations.SerializedName

data class WeatherDesc(@SerializedName("value") var value: String?)
