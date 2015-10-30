package io.dp.weather.app.activity.debug

import android.os.Bundle
import com.squareup.otto.Bus
import io.dp.weather.app.WeatherApp
import io.dp.weather.app.activity.ActivityModule
import io.dp.weather.app.activity.BaseActivity
import io.dp.weather.app.activity.BaseActivityComponent
import io.dp.weather.app.activity.DaggerActivityComponent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.textView
import javax.inject.Inject

public class DebugActivity : BaseActivity() {
    @Inject lateinit var bus: Bus

    override fun createComponent(): BaseActivityComponent {
        val app = getApplication() as WeatherApp
        val activityComponent = DaggerActivityComponent.builder()
                .appComponent(app.component)
                .activityModule(ActivityModule(this))
                .build()

        return activityComponent.plusSubComponent(DebugBusModule())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        relativeLayout {
            textView("Hello, world!") {
            }
        }

        (getComponent() as DebugBusSubcomponent).inject(this)
    }
}

