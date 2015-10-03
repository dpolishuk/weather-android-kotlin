package io.dp.weather.app.net.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

public class WeatherDesc : Parcelable {

    @Expose
    public var value: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is WeatherDesc) {
            return false
        }

        if (if (value != null) value != other.value else other.value != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        return if (value != null) value!!.hashCode() else 0
    }

    override fun toString(): String {
        return "WeatherDesc{value='$value'}"
    }

    public constructor() {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.value)
    }

    private constructor(`in`: Parcel) {
        this.value = `in`.readString()
    }

    companion object {

        public val CREATOR: Parcelable.Creator<WeatherDesc> = object : Parcelable.Creator<WeatherDesc> {
            override fun createFromParcel(source: Parcel): WeatherDesc {
                return WeatherDesc(source)
            }

            override fun newArray(size: Int): Array<WeatherDesc?> {
                return arrayOfNulls(size)
            }
        }
    }
}
