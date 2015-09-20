package io.dp.weather.app.net.dto

import com.google.gson.annotations.Expose

import android.os.Parcel
import android.os.Parcelable

public data class Forecast() : Parcelable {

    @Expose
    public var data: Data? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is Forecast) {
            return false
        }

        if (if (data != null) data != o.data else o.data != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        return if (data != null) data!!.hashCode() else 0
    }

    override fun toString(): String {
        return "Forecast{data=$data}"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(data, flags)
    }

    private constructor(`in`: Parcel) : this() {
        this.data = `in`.readParcelable<Data>(Data::class.java.getClassLoader())
    }

    companion object {

        public val CREATOR: Parcelable.Creator<Forecast> = object : Parcelable.Creator<Forecast> {
            override fun createFromParcel(source: Parcel): Forecast {
                return Forecast(source)
            }

            override fun newArray(size: Int): Array<Forecast?> {
                return arrayOfNulls(size)
            }
        }
    }
}
