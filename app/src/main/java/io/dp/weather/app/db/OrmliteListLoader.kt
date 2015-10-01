package io.dp.weather.app.db

import android.content.Context
import android.support.v4.content.AsyncTaskLoader

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.stmt.PreparedQuery

import java.sql.SQLException
import java.util.*

/**
 * Created by dp on 09/10/14.
 */
public class OrmliteListLoader<T, ID>(context: Context, dao: Dao<T, ID>, query: PreparedQuery<T>) : AsyncTaskLoader<List<T>>(context) {

    private lateinit var dao: Dao<T, ID>
    private lateinit var query: PreparedQuery<T>
    private var data: List<T>? = null

    init {
        this.dao = dao
        this.query = query
    }

    override fun loadInBackground(): List<T> {
        val result: ArrayList<T> = ArrayList<T>()

        try {
            if (query != null) {
                result.addAll(dao!!.query(query))
            } else {
                result.addAll(dao!!.queryForAll())
            }

        } catch (e: SQLException) {
        }

        return result
    }

    override fun deliverResult(datas: List<T>?) {
        if (isReset()) {
            // An async query came in while the loader is stopped. We
            // don't need the result.
            if (datas != null) {
                onReleaseResources(datas)
            }
        }

        val oldDatas = data
        data = datas

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(datas)
        }

        if (oldDatas != null && !oldDatas.isEmpty()) {
            onReleaseResources(oldDatas)
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    override fun onStartLoading() {
        if (data != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(data)
        } else {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad()
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    override fun onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad()
    }

    /**
     * Handles a request to cancel a load.
     */
    override fun onCanceled(datas: List<T>) {
        super.onCanceled(datas)

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(datas)
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    override fun onReset() {
        super.onReset()

        // Ensure the loader is stopped
        onStopLoading()

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (data != null) {
            onReleaseResources(data)
            data = null
        }
    }

    /**
     * Helper function to take care of releasing resources associated with an actively loaded data
     * set.
     */
    protected fun onReleaseResources(datas: List<T>?) {
        // For a simple List<> there is nothing to do. For something
        // like a Cursor, we would close it here.
    }
}
