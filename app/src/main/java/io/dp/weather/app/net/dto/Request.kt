package io.dp.weather.app.net.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

public class Request : Parcelable {

    @SerializedName("query")
    public var query: String? = null

    @SerializedName("type")
    public var type: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Request) {
            return false
        }

        if (if (query != null) query != other.query else other.query != null) {
            return false
        }
        if (if (type != null) type != other.type else other.type != null) {
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
