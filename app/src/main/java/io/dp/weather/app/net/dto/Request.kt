package io.dp.weather.app.net.dto

import com.google.gson.annotations.SerializedName

data class Request(@SerializedName("query") var query: String?,
                   @SerializedName("type") var type: String?)
