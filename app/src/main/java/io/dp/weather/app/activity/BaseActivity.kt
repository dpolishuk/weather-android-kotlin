package io.dp.weather.app.activity

import android.os.Bundle
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import io.dp.weather.app.BusModule
import io.dp.weather.app.WeatherApp

open class BaseActivity : RxAppCompatActivity(), HasComponent<BaseActivityComponent> {
    private lateinit var component: BaseActivityComponent

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.component = createComponent()
    }

    override fun getComponent(): BaseActivityComponent {
        return component
    }

    override fun createComponent(): BaseActivityComponent {
        val app = getApplication() as WeatherApp
        val component = DaggerActivityComponent.builder().appComponent(app.component).activityModule(ActivityModule(this)).build()

        return component.plusSubComponent(BusModule())
    }
}
