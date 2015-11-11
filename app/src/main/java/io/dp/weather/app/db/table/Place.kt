package io.dp.weather.app.db.table

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "places")
data class Place(@DatabaseField(generatedId = true, dataType = com.j256.ormlite.field.DataType.LONG, columnName = Place.ID)
                 public var id: Long? = 0,

                 @DatabaseField(dataType = com.j256.ormlite.field.DataType.STRING, columnName = Place.NAME)
                 public var name: String? = null,

                 @DatabaseField(dataType = com.j256.ormlite.field.DataType.DOUBLE_OBJ, columnName = Place.LAT)
                 public var lat: Double? = null,

                 @DatabaseField(dataType = com.j256.ormlite.field.DataType.DOUBLE_OBJ, columnName = Place.LON)
                 public var lon: Double? = null) {

    public constructor(name: String, lat: Double, lon: Double) : this() {
        this.name = name
        this.lat = lat
        this.lon = lon
    }

    companion object {
        const public val ID = "id"
        const public val NAME = "name"
        const public val LAT = "lat"
        const public val LON = "lon"
    }
}
