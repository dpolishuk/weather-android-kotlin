package io.dp.weather.app.activity

import dagger.Component
import io.dp.weather.app.AppComponent
import io.dp.weather.app.BusModule
import io.dp.weather.app.BusSubcomponent
import io.dp.weather.app.annotation.PerActivity

/**
 * Created by deepol on 19/08/15.
 */
@PerActivity
@Component(modules = arrayOf(ActivityModule::class), dependencies = arrayOf(AppComponent::class))
public interface ActivityComponent : BaseActivityComponent {

    public fun plus(module: BusModule): BusSubcomponent

//    public fun plus(module: DebugBusModule): DebugBusSubcomponent
}
