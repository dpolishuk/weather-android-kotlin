package io.dp.weather.app.net.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import android.os.Parcel
import android.os.Parcelable

import java.util.ArrayList

public data class Data() : Parcelable {

    @SerializedName("current_condition")
    @Expose
    public val currentCondition: List<CurrentCondition> = ArrayList()
    @Expose
    public val request: List<Request> = ArrayList()
    @Expose
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
