package io.dp.weather.app

import android.app.Application

/**
 * Created by dp on 07/10/14.
 */
public open class WeatherApp : Application() {

    public var component: AppComponent? = null
        private set

    override fun onCreate() {
        super.onCreate()

        this.component = createComponent()
    }

    public open fun createComponent(): AppComponent {
        return DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }
}
