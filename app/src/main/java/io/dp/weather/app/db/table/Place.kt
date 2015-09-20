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

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is Place) {
            return false
        }

        if (id != o.id) {
            return false
        }
        if (if (lat != null) lat != o.lat else o.lat != null) {
            return false
        }
        if (if (lon != null) lon != o.lon else o.lon != null) {
            return false
        }
        if (if (name != null) name != o.name else o.name != null) {
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

        public val ID: String = "id"
        public val NAME: String = "name"
        private val LAT = "lat"
        private val LON = "lon"
    }
}
