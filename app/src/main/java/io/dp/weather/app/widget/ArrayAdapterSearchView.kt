package io.dp.weather.app.widget

import android.content.Context
import android.support.v4.widget.CursorAdapter
import android.support.v7.widget.SearchView
import android.util.AttributeSet
import android.widget.AdapterView
import android.widget.ArrayAdapter

class ArrayAdapterSearchView : SearchView {

    private lateinit var searchAutoComplete: SearchView.SearchAutoComplete

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    fun initialize() {
        searchAutoComplete = findViewById(
                android.support.v7.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete
        this.setAdapter(null)
        this.setOnItemClickListener(null)
    }

    override fun setSuggestionsAdapter(adapter: CursorAdapter) {
        // don't let anyone touch this
    }

    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener?) {
        searchAutoComplete.onItemClickListener = listener
    }

    fun setAdapter(adapter: ArrayAdapter<*>?) = searchAutoComplete.setAdapter<ArrayAdapter<*>>(adapter)

    fun setText(text: String) = searchAutoComplete.setText(text)
}