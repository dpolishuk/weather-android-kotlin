package io.dp.weather.app

import android.support.v4.app.FragmentActivity
import com.trello.rxlifecycle.components.ActivityLifecycleProvider
import io.dp.weather.app.annotation.IOSched
import io.dp.weather.app.annotation.PerActivity
import io.dp.weather.app.annotation.UISched
import rx.Observable
import rx.Scheduler
import javax.inject.Inject

/**
 * Created by deepol on 24/08/15.
 */
@PerActivity
public class SchedulersManager
@Inject
constructor(private val activity: FragmentActivity,
            @IOSched private val ioScheduler: Scheduler,
            @UISched private val uiScheduler: Scheduler) {

    public fun <T> applySchedulers(): Observable.Transformer<T, T>
            = Observable.Transformer { observable ->
        (observable as Observable)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .compose((activity as ActivityLifecycleProvider).bindToLifecycle<T>())
    }
}
