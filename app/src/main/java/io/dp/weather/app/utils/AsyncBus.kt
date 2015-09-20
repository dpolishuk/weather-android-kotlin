package io.dp.weather.app.utils

import android.os.Handler
import android.os.Looper
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer

/**
 * Created by dp on 09/10/14.
 */
public class AsyncBus(enforcer: ThreadEnforcer, name: String) : Bus(enforcer, name) {

    private val mainThread = Handler(Looper.getMainLooper())

    override fun post(event: Any) {
        mainThread.post(object : Runnable {
            override fun run() {
                super@AsyncBus.post(event)
            }
        })
    }

    public fun postDelayed(event: Any, delayMs: Long) {
        mainThread.postDelayed(object : Runnable {
            override fun run() {
                super@AsyncBus.post(event)
            }
        }, delayMs)
    }
}
