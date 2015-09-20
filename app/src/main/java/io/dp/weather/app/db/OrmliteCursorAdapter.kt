package io.dp.weather.app.db

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.CursorAdapter
import android.view.View

import com.j256.ormlite.android.AndroidDatabaseResults
import com.j256.ormlite.stmt.PreparedQuery

import java.sql.SQLException

/**
 * Created by dp on 09/10/14.
 */
public abstract class OrmliteCursorAdapter<T>(context: Context, c: Cursor?, public var query: PreparedQuery<T>?) : CursorAdapter(context, c, false) {

    override public fun bindView(itemView: View, context: Context, cursor: Cursor) {
        try {
            var item = query?.mapRow(AndroidDatabaseResults(cursor, null))
            bindView(itemView, context, item)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    override public fun getItem(position: Int): T? {
        val c = super.getItem(position) as Cursor
        try {
            return query?.mapRow(AndroidDatabaseResults(c, null))
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }

    public abstract fun bindView(itemView: View, context: Context, item: T?)
}
