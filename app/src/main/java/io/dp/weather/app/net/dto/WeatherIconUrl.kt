package io.dp.weather.app

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

public class WeatherIconUrl : Parcelable {

    @Expose
    public var value: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is WeatherIconUrl) {
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
        return "WeatherIconUrl{value='$value'}"
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

        public val CREATOR: Parcelable.Creator<WeatherIconUrl> = object : Parcelable.Creator<WeatherIconUrl> {
            override fun createFromParcel(source: Parcel): WeatherIconUrl {
                return WeatherIconUrl(source)
            }

            override fun newArray(size: Int): Array<WeatherIconUrl?> {
                return arrayOfNulls(size)
            }
        }
    }
}
