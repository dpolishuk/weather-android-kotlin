package io.dp.weather.app.net.dto

data class Data(@SerializedName("currentCondition") var currentCondition: List<CurrentCondition>?,
                @SerializedName("request") var request: List<Request>?,
                @SerializedName("weather") var weather: List<Weather>?)
