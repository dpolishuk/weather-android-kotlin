package io.dp.weather.app.adapter


import android.database.Cursor
import android.database.DataSetObserver
import android.support.v7.widget.RecyclerView

abstract class CursorRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(cursor: Cursor?) : RecyclerView.Adapter<VH>() {

    private var cursor: Cursor?
    private var dataValid: Boolean
    private var rowIdColumn: Int
    private val dataSetObserver: DataSetObserver?

    init {
        this.cursor = cursor
        dataValid = cursor != null
        rowIdColumn = if (dataValid) this.cursor!!.getColumnIndex("_id") else -1
        dataSetObserver = NotifyingDataSetObserver()
        this.cursor?.registerDataSetObserver(dataSetObserver)
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    override fun getItemId(position: Int): Long {
        if (dataValid && cursor?.moveToPosition(position) ?: false) {
            return cursor?.getLong(rowIdColumn) ?: 0
        }
        return 0
    }

    fun getItem(position: Int): Any? {
        when {
            dataValid -> {
                cursor?.moveToPosition(position)
                return cursor
            }
            else -> return null
        }
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    abstract fun onBindViewHolder(viewHolder: VH, cursor: Cursor?)

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        if (!dataValid) {
            throw IllegalStateException("this should only be called when the cursor is valid")
        }
        if (!cursor!!.moveToPosition(position)) {
            throw IllegalStateException("couldn't move cursor to position " + position)
        }
        onBindViewHolder(viewHolder, cursor)
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    open fun changeCursor(cursor: Cursor?) {
        if (cursor != null) {
            val old = swapCursor(cursor)
            old?.close()
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * [.changeCursor], the returned old Cursor is *not*
     * closed.
     */
    fun swapCursor(newCursor: Cursor): Cursor? {
        if (newCursor === cursor) {
            return null
        }
        val oldCursor = cursor
        if (oldCursor != null && dataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(dataSetObserver)
        }
        cursor = newCursor
        if (cursor != null) {
            if (dataSetObserver != null) {
                cursor!!.registerDataSetObserver(dataSetObserver)
            }
            rowIdColumn = newCursor.getColumnIndexOrThrow("_id")
            dataValid = true
            notifyDataSetChanged()
        } else {
            rowIdColumn = -1
            dataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor
    }

    private inner class NotifyingDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            dataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            dataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}