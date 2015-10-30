package io.dp.weather.app.db

import com.j256.ormlite.stmt.PreparedQuery
import io.dp.weather.app.db.table.Place
import java.sql.SQLException

/**
 * Created by dp on 09/10/14.
 */
public object Queries {

    public fun prepareCityQuery(dbHelper: DatabaseHelper?): PreparedQuery<Place>? {
        try {
            val qb = dbHelper!!.getPlaceDao()!!.queryBuilder()
            return qb.prepare()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }
}
