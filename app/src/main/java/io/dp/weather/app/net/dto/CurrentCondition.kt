package io.dp.weather.app.net.dto

import com.google.gson.annotations.SerializedName
import io.dp.weather.app.WeatherIconUrl
import java.util.*

public data class CurrentCondition(@SerializedName("cloudcover") var cloudcover: String?,
                                   @SerializedName("humidity") var humidity: String?,
                                   @SerializedName("observation_time") var observationTime: String?,
                                   @SerializedName("precipMM") var precipMM: String?,
                                   @SerializedName("temp_C") var tempC: String?,
                                   @SerializedName("temp_F") var tempF: String?,
                                   @SerializedName("pressure") var pressure: String?,
                                   @SerializedName("visibility") var visibility: String?,
                                   @SerializedName("weatherCode") var weatherCode: String?,
                                   @SerializedName("weatherDesc") var weatherDesc: List<WeatherDesc>?,
                                   @SerializedName("weatherIconUrl") var weatherIconUrl: List<WeatherIconUrl>?,
                                   @SerializedName("winddir16Point") var winddir16Point: String?,
                                   @SerializedName("winddirDegree") var winddirDegree: String?,
                                   @SerializedName("windspeedKmph") var windspeedKmph: String?,
                                   @SerializedName("windspeedMiles") var windspeedMiles: String?)
