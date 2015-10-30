package io.dp.weather.app.activity

import android.os.Bundle
import com.trello.rxlifecycle.components.support.RxFragmentActivity
import io.dp.weather.app.WeatherApp
import io.dp.weather.app.activity.debug.DebugBusModule

/**
 * Created by deepol on 11/09/15.
 */
class MockActivity : RxFragmentActivity(), HasComponent<BaseActivityComponent> {
    internal var component: BaseActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.component = createComponent()
    }

    override fun createComponent(): BaseActivityComponent {
        val app = application as WeatherApp
        val component = DaggerActivityComponent.builder().appComponent(app.component).activityModule(ActivityModule(this)).build()

        return component.plusSubComponent(DebugBusModule())
    }

    override fun getComponent(): BaseActivityComponent {
        return component
    }
}
