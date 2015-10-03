package io.dp.weather.app.activity.debug

import dagger.Subcomponent
import io.dp.weather.app.BusSubcomponent
import io.dp.weather.app.annotation.PerActivity

/**
 * Created by deepol on 16/09/15.
 */
@PerActivity
@Subcomponent(modules = arrayOf(DebugBusModule::class))
public interface DebugBusSubcomponent : BusSubcomponent {

    public fun inject(debugActivity: DebugActivity)
}
