package io.dp.weather.app.db

import android.database.Cursor
import android.database.CursorWrapper
import android.provider.BaseColumns

/**
 * Created by dp on 09/10/14.
 */
public class NoIdCursorWrapper : CursorWrapper {

    private var idColumnIndex: Int = 0

    /**
     * Create a NoIdCursorWrapper using the alias column index.

     * @param c             the cursor to wrap
     * *
     * @param idColumnIndex the column index to use as the _id column alias
     */
    public constructor(c: Cursor, idColumnIndex: Int) : super(c) {
        this.idColumnIndex = idColumnIndex
    }

    /**
     * Create a NoIdCursorWrapper using the alias column name.

     * @param c            the cursor to wrap
     * *
     * @param idColumnName the column name to use as the _id column alias
     */
    public constructor(c: Cursor, idColumnName: String) : super(c) {
        idColumnIndex = c.getColumnIndex(idColumnName)
    }

    override fun getColumnIndex(columnName: String): Int {
        var index = super.getColumnIndex(columnName)
        if (index < 0 && BaseColumns._ID == columnName) {
            index = idColumnIndex
        }
        return index
    }

    @Throws(IllegalArgumentException::class)
    override fun getColumnIndexOrThrow(columnName: String): Int {
        val index = getColumnIndex(columnName)
        if (index >= 0) {
            return index
        }
        // let the AbstractCursor generate the exception
        return super.getColumnIndexOrThrow(columnName)
    }
}
