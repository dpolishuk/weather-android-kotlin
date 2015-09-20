package io.dp.weather.app

import com.google.gson.annotations.Expose

import android.os.Parcel
import android.os.Parcelable

public class WeatherIconUrl : Parcelable {

    @Expose
    public var value: String? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is WeatherIconUrl) {
            return false
        }

        if (if (value != null) value != o.value else o.value != null) {
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
