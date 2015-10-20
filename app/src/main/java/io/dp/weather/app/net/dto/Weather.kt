package io.dp.weather.app.net.dto

import io.dp.weather.app.WeatherIconUrl

data class Weather(@SerializedName("date") var date: String?,
                   @SerializedName("precipMM") var precipMM: String?,
                   @SerializedName("tempMaxC") var tempMaxC: String?,
                   @SerializedName("tempMaxF") var tempMaxF: String?,
                   @SerializedName("tempMinC") var tempMinC: String?,
                   @SerializedName("tempMinF") var tempMinF: String?,
                   @SerializedName("weatherCode") var weatherCode: String?,
                   @SerializedName("weatherDesc") var weatherDesc: List<WeatherDesc>,
                   @SerializedName("weatherIconUrl") var weatherIconUrl: List<WeatherIconUrl>,
                   @SerializedName("winddir16Point") var winddir16Point: String?,
                   @SerializedName("winddirDegree") var winddirDegree: String?,
                   @SerializedName("winddirection") var winddirection: String?,
                   @SerializedName("windspeedKmph") var windspeedKmph: String?,
                   @SerializedName("windspeedMiles") var windspeedMiles: String?)
