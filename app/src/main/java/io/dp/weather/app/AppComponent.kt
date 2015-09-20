package io.dp.weather.app

import android.location.Geocoder
import com.google.gson.Gson
import dagger.Component
import io.dp.weather.app.annotation.IOSched
import io.dp.weather.app.annotation.UISched
import io.dp.weather.app.net.PlacesApi
import io.dp.weather.app.net.WeatherApi
import rx.Scheduler
import javax.inject.Singleton

/**
 * Created by deepol on 19/08/15.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
public interface AppComponent {

    public fun provideGson(): Gson

    public fun proviceGeocoder(): Geocoder

    public fun provideForecastApi(): WeatherApi

    public fun providePlacesApi(): PlacesApi

    @IOSched public fun provideIoScheduler(): Scheduler

    @UISched public fun provideUiScheduler(): Scheduler
}
