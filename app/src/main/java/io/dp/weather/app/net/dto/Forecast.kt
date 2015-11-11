package io.dp.weather.app.net.dto

import com.google.gson.annotations.SerializedName

public data class Forecast(@SerializedName("data") var data: Data? = null)
