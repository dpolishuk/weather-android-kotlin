package io.dp.weather.app.db

import android.content.Context
import android.database.Cursor
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import com.j256.ormlite.android.AndroidDatabaseResults
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.stmt.PreparedQuery
import java.io.FileDescriptor
import java.io.PrintWriter
import java.sql.SQLException

/**
 * Created by dp on 09/10/14.
 */
public class OrmliteCursorLoader<T>(context: Context, public val dao: Dao<T, *>?,
                                    public val query: PreparedQuery<T>?) : AsyncTaskLoader<Cursor>(context) {

    val observer: Loader.ForceLoadContentObserver = Loader<Cursor>(context).ForceLoadContentObserver()

    private var cursor: Cursor? = null

    /* Runs on a worker thread */

    override fun loadInBackground(): Cursor? {
        var cursor: Cursor? = null
        try {
            cursor = getCursor(query)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.count
            registerContentObserver(cursor)
        }
        return cursor
    }

    @Throws(SQLException::class)
    public fun getCursor(query: PreparedQuery<T>?): Cursor {
        val baseDao = dao as BaseDaoImpl<T, *>
        val iterator = dao.iterator(query)
        val results = iterator.getRawResults() as AndroidDatabaseResults
        val idColumnName = baseDao.getTableInfo().getIdField().getColumnName()
        return NoIdCursorWrapper(results.getRawCursor(), idColumnName)
    }

    /**
     * Registers an observer to get notifications from the content provider when the cursor needs to
     * be refreshed.
     */
    fun registerContentObserver(cursor: Cursor) {
        cursor.registerContentObserver(this.observer)
    }

    /* Runs on the UI thread */
    override public fun deliverResult(cursor: Cursor?) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            cursor?.close()
            return
        }
        val oldCursor = this.cursor
        this.cursor = cursor

        if (isStarted()) {
            super.deliverResult(cursor)
        }

        if (oldCursor != null && oldCursor !== cursor && !oldCursor.isClosed) {
            oldCursor.close()
        }
    }

    override fun onStartLoading() {
        if (cursor != null) {
            deliverResult(cursor)
        }
        if (takeContentChanged() || cursor == null) {
            forceLoad()
        }
    }

    /**
     * Must be called from the UI thread
     */
    override fun onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad()
    }

    override fun onCanceled(cursor: Cursor?) {
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
    }

    override fun onReset() {
        super.onReset()

        // Ensure the loader is stopped
        onStopLoading()

        if (cursor != null && !cursor!!.isClosed) {
            cursor!!.close()
        }
        cursor = null
    }

    override fun dump(prefix: String, fd: FileDescriptor, writer: PrintWriter, args: Array<String>) {
        super.dump(prefix, fd, writer, args)
        writer.print(prefix)
        writer.print("cursor=")
        writer.println(cursor)
    }
}
