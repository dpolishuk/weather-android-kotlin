package io.dp.weather.app.utils

import android.view.MenuItem
import android.widget.PopupMenu

operator fun PopupMenu.invoke(f: PopupMenu.() -> Unit) = f()

fun android.widget.PopupMenu.onMenuItemClick(init: __PopupMenu_OnMenuItemClickListener.() -> Boolean) {
    val listener = __PopupMenu_OnMenuItemClickListener()
    listener.init()
    setOnMenuItemClickListener(listener)
}

class __PopupMenu_OnMenuItemClickListener : android.widget.PopupMenu.OnMenuItemClickListener {
    private var _onMenuItemClick: ((MenuItem?) -> Boolean)? = null

    public fun onMenuItemClick(listener: (MenuItem?) -> Boolean) {
        _onMenuItemClick = listener
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return _onMenuItemClick?.invoke(item) ?: false
    }
}
