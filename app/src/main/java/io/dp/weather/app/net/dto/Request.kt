package io.dp.weather.app.net.dto

import com.google.gson.annotations.Expose

import android.os.Parcel
import android.os.Parcelable

public class Request : Parcelable {

    @Expose
    public var query: String? = null
    @Expose
    public var type: String? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is Request) {
            return false
        }

        if (if (query != null) query != o.query else o.query != null) {
            return false
        }
        if (if (type != null) type != o.type else o.type != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = if (query != null) query!!.hashCode() else 0
        result = 31 * result + (if (type != null) type!!.hashCode() else 0)
        return result
    }

    override fun toString(): String {
        return "Request{query='$query', type='$type'}"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.query)
        dest.writeString(this.type)
    }

    public constructor() {
    }

    private constructor(`in`: Parcel) {
        this.query = `in`.readString()
        this.type = `in`.readString()
    }

    companion object {

        public val CREATOR: Parcelable.Creator<Request> = object : Parcelable.Creator<Request> {
            override fun createFromParcel(source: Parcel): Request {
                return Request(source)
            }

            override fun newArray(size: Int): Array<Request?> {
                return arrayOfNulls(size)
            }
        }
    }
}
