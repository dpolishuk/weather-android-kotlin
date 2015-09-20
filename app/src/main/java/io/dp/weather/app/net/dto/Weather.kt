package io.dp.weather.app.net.dto

import com.google.gson.annotations.Expose

import android.os.Parcel
import android.os.Parcelable

import java.util.ArrayList

import io.dp.weather.app.WeatherIconUrl

public class Weather : Parcelable {

    @Expose
    public var date: String? = null
    @Expose
    public var precipMM: String? = null
    @Expose
    public var tempMaxC: String? = null
    @Expose
    public var tempMaxF: String? = null
    @Expose
    public var tempMinC: String? = null
    @Expose
    public var tempMinF: String? = null
    @Expose
    public var weatherCode: String? = null
    @Expose
    public var weatherDesc: List<WeatherDesc>? = ArrayList()
    @Expose
    public var weatherIconUrl: List<WeatherIconUrl>? = ArrayList()
    @Expose
    public var winddir16Point: String? = null
    @Expose
    public var winddirDegree: String? = null
    @Expose
    public var winddirection: String? = null
    @Expose
    public var windspeedKmph: String? = null
    @Expose
    public var windspeedMiles: String? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is Weather) {
            return false
        }

        if (if (date != null) date != o.date else o.date != null) {
            return false
        }
        if (if (precipMM != null) precipMM != o.precipMM else o.precipMM != null) {
            return false
        }
        if (if (tempMaxC != null) tempMaxC != o.tempMaxC else o.tempMaxC != null) {
            return false
        }
        if (if (tempMaxF != null) tempMaxF != o.tempMaxF else o.tempMaxF != null) {
            return false
        }
        if (if (tempMinC != null) tempMinC != o.tempMinC else o.tempMinC != null) {
            return false
        }
        if (if (tempMinF != null) tempMinF != o.tempMinF else o.tempMinF != null) {
            return false
        }
        if (if (weatherCode != null) weatherCode != o.weatherCode
        else o.weatherCode != null) {
            return false
        }
        if (if (weatherDesc != null) weatherDesc != o.weatherDesc
        else o.weatherDesc != null) {
            return false
        }
        if (if (weatherIconUrl != null) weatherIconUrl != o.weatherIconUrl
        else o.weatherIconUrl != null) {
            return false
        }
        if (if (winddir16Point != null) winddir16Point != o.winddir16Point
        else o.winddir16Point != null) {
            return false
        }
        if (if (winddirDegree != null) winddirDegree != o.winddirDegree
        else o.winddirDegree != null) {
            return false
        }
        if (if (winddirection != null) winddirection != o.winddirection
        else o.winddirection != null) {
            return false
        }
        if (if (windspeedKmph != null) windspeedKmph != o.windspeedKmph
        else o.windspeedKmph != null) {
            return false
        }
        if (if (windspeedMiles != null) windspeedMiles != o.windspeedMiles
        else o.windspeedMiles != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = if (date != null) date!!.hashCode() else 0
        result = 31 * result + (if (precipMM != null) precipMM!!.hashCode() else 0)
        result = 31 * result + (if (tempMaxC != null) tempMaxC!!.hashCode() else 0)
        result = 31 * result + (if (tempMaxF != null) tempMaxF!!.hashCode() else 0)
        result = 31 * result + (if (tempMinC != null) tempMinC!!.hashCode() else 0)
        result = 31 * result + (if (tempMinF != null) tempMinF!!.hashCode() else 0)
        result = 31 * result + (if (weatherCode != null) weatherCode!!.hashCode() else 0)
        result = 31 * result + (if (weatherDesc != null) weatherDesc!!.hashCode() else 0)
        result = 31 * result + (if (weatherIconUrl != null) weatherIconUrl!!.hashCode() else 0)
        result = 31 * result + (if (winddir16Point != null) winddir16Point!!.hashCode() else 0)
        result = 31 * result + (if (winddirDegree != null) winddirDegree!!.hashCode() else 0)
        result = 31 * result + (if (winddirection != null) winddirection!!.hashCode() else 0)
        result = 31 * result + (if (windspeedKmph != null) windspeedKmph!!.hashCode() else 0)
        result = 31 * result + (if (windspeedMiles != null) windspeedMiles!!.hashCode() else 0)
        return result
    }

    override fun toString(): String {
        return "Weather{date='$date', precipMM='$precipMM', tempMaxC='$tempMaxC', tempMaxF='$tempMaxF', tempMinC='$tempMinC', tempMinF='$tempMinF', weatherCode='$weatherCode', weatherDesc=$weatherDesc, weatherIconUrl=$weatherIconUrl, winddir16Point='$winddir16Point', winddirDegree='$winddirDegree', winddirection='$winddirection', windspeedKmph='$windspeedKmph', windspeedMiles='$windspeedMiles'}"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.date)
        dest.writeString(this.precipMM)
        dest.writeString(this.tempMaxC)
        dest.writeString(this.tempMaxF)
        dest.writeString(this.tempMinC)
        dest.writeString(this.tempMinF)
        dest.writeString(this.weatherCode)
        dest.writeTypedList(weatherDesc)
        dest.writeTypedList(weatherIconUrl)
        dest.writeString(this.winddir16Point)
        dest.writeString(this.winddirDegree)
        dest.writeString(this.winddirection)
        dest.writeString(this.windspeedKmph)
        dest.writeString(this.windspeedMiles)
    }

    public constructor() {
    }

    private constructor(`in`: Parcel) {
        this.date = `in`.readString()
        this.precipMM = `in`.readString()
        this.tempMaxC = `in`.readString()
        this.tempMaxF = `in`.readString()
        this.tempMinC = `in`.readString()
        this.tempMinF = `in`.readString()
        this.weatherCode = `in`.readString()
        `in`.readTypedList(weatherDesc, WeatherDesc.CREATOR)
        `in`.readTypedList(weatherIconUrl, WeatherIconUrl.CREATOR)
        this.winddir16Point = `in`.readString()
        this.winddirDegree = `in`.readString()
        this.winddirection = `in`.readString()
        this.windspeedKmph = `in`.readString()
        this.windspeedMiles = `in`.readString()
    }

    companion object {

        public val CREATOR: Parcelable.Creator<Weather> = object : Parcelable.Creator<Weather> {
            override fun createFromParcel(source: Parcel): Weather {
                return Weather(source)
            }

            override fun newArray(size: Int): Array<Weather?> {
                return arrayOfNulls(size)
            }
        }
    }
}
