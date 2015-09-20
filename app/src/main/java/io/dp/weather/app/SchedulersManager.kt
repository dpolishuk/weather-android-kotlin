package io.dp.weather.app

import com.trello.rxlifecycle.components.ActivityLifecycleProvider
import io.dp.weather.app.annotation.IOSched
import io.dp.weather.app.annotation.PerActivity
import io.dp.weather.app.annotation.UISched
import javax.inject.Inject
import rx.Observable
import rx.Scheduler

/**
 * Created by deepol on 24/08/15.
 */
@PerActivity
public class SchedulersManager
@Inject
constructor(@IOSched private val ioScheduler: Scheduler, @UISched private val uiScheduler: Scheduler) {

    public fun <T> applySchedulers(provider: ActivityLifecycleProvider): Observable.Transformer<T, T>? {
        return null;
//        return { observable -> (observable as Observable).subscribeOn(ioScheduler).observeOn(uiScheduler).compose(provider.bindToLifecycle()) }
    }
}
