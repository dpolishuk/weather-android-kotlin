package io.dp.weather.app.activity.debug

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import dagger.Module
import dagger.Provides
import io.dp.weather.app.annotation.PerActivity
import io.dp.weather.app.utils.AsyncBus

/**
 * Created by deepol on 10/09/15.
 */
@Module
public class DebugBusModule {

    @Provides
    @PerActivity
    // in Kotlin we need to have different method name for overriden dependency
    // the reason - dagger2 + kapt won't generate BusModule_Factory code
    public fun provideDebugBus(): Bus {
        return AsyncBus(ThreadEnforcer.ANY, "debug")
    }

}
