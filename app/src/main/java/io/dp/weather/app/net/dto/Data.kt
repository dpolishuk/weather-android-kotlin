package io.dp.weather.app.net.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

public data class Data() : Parcelable {

    @SerializedName("current_condition")
    public val currentCondition: List<CurrentCondition> = ArrayList()

    @SerializedName("request")
    public val request: List<Request> = ArrayList()

    @SerializedName("weather")
    public val weather: List<Weather> = ArrayList()

    override fun toString(): String {
        return "Data{currentCondition=$currentCondition, request=$request, weather=$weather}"
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        throw UnsupportedOperationException()
    }

    override fun describeContents(): Int {
        throw UnsupportedOperationException()
    }

}
