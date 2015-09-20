package io.dp.weather.app.activity.debug

import android.os.Bundle
import android.widget.Toast
import com.squareup.otto.Bus
import io.dp.weather.app.DebugBusSubcomponent
import io.dp.weather.app.R
import io.dp.weather.app.WeatherApp
import io.dp.weather.app.activity.ActivityModule
import io.dp.weather.app.activity.BaseActivity
import io.dp.weather.app.activity.BaseActivityComponent
import javax.inject.Inject

public class DebugActivity : BaseActivity() {
    @Inject
    lateinit val bus: Bus

    override fun createComponent(): BaseActivityComponent {
        val app = getApplication() as WeatherApp
        val activityComponent = DaggerActivityComponent.builder().appComponent(app.component).activityModule(ActivityModule(this)).build()

        return activityComponent.plus(DebugBusModule())
    }

    override fun onCreate(state: Bundle) {
        super.onCreate(state)
        setContentView(R.layout.activity_debug)
        (getComponent() as DebugBusSubcomponent).inject(this)
    }
}

