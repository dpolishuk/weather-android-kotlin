package io.dp.weather.app

import android.app.Application
import android.location.Geocoder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.dp.weather.app.annotation.IOSched
import io.dp.weather.app.annotation.UISched
import io.dp.weather.app.net.PlacesApi
import io.dp.weather.app.net.WeatherApi
import retrofit.RestAdapter
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Singleton

/**
 * Created by dp on 07/10/14.
 */
@Module public class AppModule(application: WeatherApp) {

    protected val application: Application

    init {
        this.application = application

        Timber.plant(Timber.DebugTree())
    }

    @Provides @Singleton public fun proviceGeocoder(): Geocoder {
        return Geocoder(application)
    }

    @Provides @Singleton public fun provideApplication(): Application {
        return application
    }

    @Provides @Singleton public fun provideForecastApi(): WeatherApi {
        val b = RestAdapter.Builder()

        if (BuildConfig.DEBUG) {
            b.setLogLevel(RestAdapter.LogLevel.FULL)
        }

        b.setRequestInterceptor({ request ->
            request.addQueryParam("key", BuildConfig.FORECAST_API_KEY)
            request.addQueryParam("format", "json")
        })

        val restAdapter = b.setEndpoint(BuildConfig.FORECAST_API_URL).build()
        return restAdapter.create(WeatherApi::class.java)
    }

    @Provides @Singleton public fun providePlacesApi(): PlacesApi {
        val b = RestAdapter.Builder()

        if (BuildConfig.DEBUG) {
            b.setLogLevel(RestAdapter.LogLevel.FULL)
        }

        b.setRequestInterceptor({ request ->
            request.addQueryParam("key", BuildConfig.PLACES_API_KEY)
            request.addQueryParam("sensor", "false")
        })

        val restAdapter = b.setEndpoint(BuildConfig.PLACES_API_URL).build()
        return restAdapter.create(PlacesApi::class.java)
    }

    @Provides @Singleton public fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides @Singleton @IOSched public fun provideIoScheduler(): Scheduler {
        return Schedulers.io()
    }

    @Provides @Singleton @UISched public fun provideUiScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}
