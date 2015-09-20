package io.dp.weather.app.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import io.dp.weather.app.R
import io.dp.weather.app.db.DatabaseHelper
import io.dp.weather.app.db.table.Place
import rx.Observable
import rx.lang.kotlin.observable
import timber.log.Timber
import java.io.IOException
import java.sql.SQLException

/**
 * Created by dp on 10/10/14.
 */
public object Observables {

    public fun getGeoForPlace(context: Context,
                              dbHelper: DatabaseHelper,
                              geocoder: Geocoder,
                              lookupPlace: String): Observable<Place> {

        return observable<List<Address>>() { subscriber ->
            try {
                Timber.v("Before ask geocoder")
                synchronized (geocoder) {
                    subscriber.onNext(geocoder.getFromLocationName(lookupPlace, 1))
                }
            } catch (e: IOException) {
                Toast.makeText(context, R.string.cannot_find_geo_for_specified_location,
                        Toast.LENGTH_SHORT).show()
                Timber.e(e, "Cannot find geo for location name")
                subscriber.onError(e)
            } finally {
                subscriber.onCompleted()
            }
        }.flatMap { addresses ->
            observable<Place>() { subscriber ->
                if (addresses != null && addresses.size() > 0) {
                    val address = addresses.get(0)
                    try {

                        val place = Place(lookupPlace, address.latitude, address.longitude)
                        dbHelper.getPlaceDao()!!.createIfNotExists(place)

                        subscriber.onNext(place)
                    } catch (e: SQLException) {
                        Toast.makeText(context,
                                R.string.something_went_wrong_with_adding_new_location,
                                Toast.LENGTH_SHORT).show()
                        Timber.e(e, "Cannot add city $address lookupName: $lookupPlace lat ${address.latitude} lon ${address.longitude}")
                        subscriber.onError(e)
                    } finally {
                        subscriber.onCompleted()
                    }
                }
            }
        }
    }
}
