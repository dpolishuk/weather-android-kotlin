package io.dp.weather.app.adapter

import android.support.v4.app.FragmentActivity
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.google.gson.JsonObject
import io.dp.weather.app.R
import io.dp.weather.app.net.PlacesApi
import java.util.*
import javax.inject.Inject

class PlacesAutoCompleteAdapter
@Inject
constructor(activity: FragmentActivity, private val placesApi: PlacesApi) :
        ArrayAdapter<String>(activity, R.layout.item_search_list), Filterable {

    private var resultList: ArrayList<String>? = null

    override fun getCount(): Int = resultList?.size ?: 0

    override fun getItem(index: Int): String = resultList?.get(index) ?: ""

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                val filterResults = Filter.FilterResults()
                if (constraint != null) {
                    val jsonResults = placesApi.getAutocomplete(constraint.toString())

                    // Create a JSON object hierarchy from the results
                    val predsJsonArray = jsonResults.getAsJsonArray("predictions")

                    // Extract the Place descriptions from the results
                    resultList = ArrayList<String>(predsJsonArray.size())
                    for (i in 0..predsJsonArray.size() - 1) {
                        val o = predsJsonArray[i] as JsonObject
                        resultList?.add(o.get("description").asString)
                    }

                    // Assign the data to the FilterResults
                    filterResults.values = resultList
                    filterResults.count = resultList?.size ?: 0
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?,
                                        results: Filter.FilterResults?) = when {
                results?.count ?: -1 > 0 -> notifyDataSetChanged()
                else -> notifyDataSetInvalidated()
            }
        }
    }
}
