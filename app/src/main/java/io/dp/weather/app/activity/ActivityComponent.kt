package io.dp.weather.app.activity

import dagger.Component
import io.dp.weather.app.AppComponent
import io.dp.weather.app.BusModule
import io.dp.weather.app.BusSubcomponent
import io.dp.weather.app.activity.debug.DebugBusModule
import io.dp.weather.app.activity.debug.DebugBusSubcomponent
import io.dp.weather.app.annotation.PerActivity

@PerActivity
@Component(modules = arrayOf(ActivityModule::class), dependencies = arrayOf(AppComponent::class))
interface ActivityComponent : BaseActivityComponent {

    fun plusSubComponent(module: BusModule): BusSubcomponent

    fun plusSubComponent(module: DebugBusModule): DebugBusSubcomponent
}
