package io.dp.weather.app.db.table

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

/**
 * Created by dp on 09/10/14.
 */

@DatabaseTable(tableName = "places")
public class Place {

    @DatabaseField(generatedId = true, dataType = DataType.LONG, columnName = ID)
    public val id: Long = 0

    @DatabaseField(dataType = DataType.STRING, columnName = NAME)
    public var name: String? = null

    @DatabaseField(dataType = DataType.DOUBLE_OBJ, columnName = LAT)
    public var lat: Double? = null

    @DatabaseField(dataType = DataType.DOUBLE_OBJ, columnName = LON)
    public var lon: Double? = null

    public constructor() {
    }

    public constructor(name: String, lat: Double, lon: Double) {
        this.name = name
        this.lat = lat
        this.lon = lon
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Place) {
            return false
        }

        if (id != other.id) {
            return false
        }
        if (if (lat != null) lat != other.lat else other.lat != null) {
            return false
        }
        if (if (lon != null) lon != other.lon else other.lon != null) {
            return false
        }
        if (if (name != null) name != other.name else other.name != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = (id xor (id.ushr(32))).toInt()
        result = 31 * result + (if (name != null) name!!.hashCode() else 0)
        result = 31 * result + (if (lat != null) lat!!.hashCode() else 0)
        result = 31 * result + (if (lon != null) lon!!.hashCode() else 0)
        return result
    }

    override fun toString(): String {
        return "Place{id=$id, name='$name', lat=$lat, lon=$lon}"
    }

    companion object {
        const public val ID = "id"
        const public val NAME = "name"
        const public val LAT = "lat"
        const public val LON = "lon"
    }
}
