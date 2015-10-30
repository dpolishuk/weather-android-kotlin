package io.dp.weather.app.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import io.dp.weather.app.BuildConfig
import io.dp.weather.app.R
import io.dp.weather.app.db.table.Place
import timber.log.Timber
import java.sql.SQLException

/**
 * Created by dp on 09/10/14.
 */
public class DatabaseHelper : OrmLiteSqliteOpenHelper {

    var context: Context

    @Volatile private var placeDao: Dao<Place, Long>? = null

    var predefinedCities = arrayOf(Place("Dublin", 53.34410, -6.2674), Place("London", 51.51121, -0.1198), Place("New York", 40.71278, -74.00594), Place("Barcelona", 41.3850, 2.1734))

    public constructor(context: Context, databaseName: String, factory: SQLiteDatabase.CursorFactory,
                       databaseVersion: Int) : super(context, databaseName, factory, databaseVersion, R.raw.ormlite_config) {
        this.context = context
    }

    public constructor(context: Context) : super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config) {
        this.context = context
    }

    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        Timber.v("! onCreateDatabase")

        try {
            TableUtils.createTableIfNotExists(connectionSource, Place::class.java)
            val cityDao = getPlaceDao()
            for (place in predefinedCities) {
                cityDao?.createIfNotExists(place)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    override fun onUpgrade(database: SQLiteDatabase, connectionSource: ConnectionSource, oldVersion: Int,
                           newVersion: Int) {
        Timber.v("! onUpgradeDatabase")
    }

    @Throws(SQLException::class)
    public fun getPlaceDao(): Dao<Place, Long>? {
        var resultDao = placeDao

        if (resultDao == null) {
            synchronized (this) {
                resultDao = placeDao

                if (resultDao == null) {
                    resultDao = getDao(Place::class.java)
                    placeDao = resultDao
                }
            }
        }
        return resultDao
    }

    override fun close() {
        super.close()

        placeDao = null
    }

    companion object {

        private val DATABASE_NAME = "weather.db"
        private val DATABASE_VERSION = BuildConfig.DATABASE_VERSION
    }
}
