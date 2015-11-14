package io.dp.weather.app.adapter

import android.database.Cursor
import android.support.v7.widget.RecyclerView
import com.j256.ormlite.android.AndroidDatabaseResults
import com.j256.ormlite.stmt.PreparedQuery
import java.sql.SQLException

abstract class OrmliteCursorRecyclerViewAdapter<T, VH : RecyclerView.ViewHolder>() : CursorRecyclerViewAdapter<VH>(null) {
    var preparedQuery: PreparedQuery<T>? = null

    abstract fun onBindViewHolder(holder: VH, t: T)

    override fun onBindViewHolder(viewHolder: VH, cursor: Cursor?) {
        try {
            onBindViewHolder(viewHolder, this.cursorToObject(cursor!!))
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun changeCursor(cursor: Cursor?) {
        this.preparedQuery = preparedQuery
        super.changeCursor(cursor)
    }

    fun changeCursor(cursor: Cursor, preparedQuery: PreparedQuery<T>) {
        this.preparedQuery = preparedQuery
        super.changeCursor(cursor)
    }

    fun getTypedItem(position: Int): T {
        try {
            return this.cursorToObject(getItem(position) as Cursor)
        } catch (var3: SQLException) {
            throw RuntimeException(var3)
        }
    }

    @Throws(SQLException::class)
    protected fun cursorToObject(cursor: Cursor): T {
        return this.preparedQuery!!.mapRow(AndroidDatabaseResults(cursor, null))
    }
}