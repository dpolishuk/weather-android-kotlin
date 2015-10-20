package io.dp.weather.app.net.dto

data class Request(@SerializedName("query") var query: String?,
                   @SerializedName("type") var type: String?)
