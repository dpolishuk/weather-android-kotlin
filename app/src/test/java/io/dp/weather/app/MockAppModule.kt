package io.dp.weather.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.otto.Bus
import dagger.Module
import dagger.Provides
import io.dp.weather.app.annotation.CachePrefs
import io.dp.weather.app.annotation.ConfigPrefs
import io.dp.weather.app.annotation.IOSched
import io.dp.weather.app.annotation.UISched
import io.dp.weather.app.db.DatabaseHelper
import io.dp.weather.app.net.PlacesApi
import io.dp.weather.app.net.WeatherApi
import io.dp.weather.app.net.dto.Forecast
import org.mockito.Mockito.mock
import retrofit.MockRestAdapter
import retrofit.RequestInterceptor
import retrofit.RestAdapter
import retrofit.http.Query
import rx.Observable
import rx.Scheduler
import rx.Subscriber
import rx.schedulers.Schedulers
import javax.inject.Singleton

/**
 * Created by dp on 08/10/14.
 */
@Module class MockAppModule(internal val app: WeatherApp) {

    @Provides @Singleton fun provideApplication(): Application {
        return app
    }

    @Provides @Singleton fun provideForecastApi(gson: Gson): WeatherApi {
        val b = RestAdapter.Builder()

        if (BuildConfig.DEBUG) {
            b.setLogLevel(RestAdapter.LogLevel.FULL)
        }

        b.setRequestInterceptor(object : RequestInterceptor {
            override fun intercept(request: RequestInterceptor.RequestFacade) {
                request.addQueryParam("key", BuildConfig.FORECAST_API_KEY)
                request.addQueryParam("format", "json")
            }
        })

        val restAdapter = b.setEndpoint(BuildConfig.FORECAST_API_URL).build()

        val mock = MockRestAdapter.from(restAdapter)

        val f = gson.fromJson(TestUtils.WEATHER_JSON, Forecast::class.javaClass)
        return mock.create(WeatherApi::class.javaClass, MockWeatherApi(f))
    }

    @Provides @Singleton fun providePlacesApi(): PlacesApi {
        val b = RestAdapter.Builder()

        if (BuildConfig.DEBUG) {
            b.setLogLevel(RestAdapter.LogLevel.FULL)
        }

        b.setRequestInterceptor(object : RequestInterceptor {
            override fun intercept(request: RequestInterceptor.RequestFacade) {
                request.addQueryParam("key", BuildConfig.PLACES_API_KEY)
                request.addQueryParam("sensor", "false")
            }
        })

        val restAdapter = b.setEndpoint(BuildConfig.PLACES_API_URL).build()
        return restAdapter.create(PlacesApi::class.java)
    }

    @Provides @Singleton fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides @Singleton fun provideBus(): Bus {
        return Bus()
    }

    @Provides @Singleton fun provideGeocoder(): Geocoder {
        return mock(Geocoder::class.java)
    }

    @Provides @Singleton @ConfigPrefs fun provideConfigPrefs(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Provides @Singleton @CachePrefs fun provideCachePrefs(): SharedPreferences {
        return app.getSharedPreferences("cachePrefs", Context.MODE_PRIVATE)
    }

    @Provides @Singleton fun provideDatabaseHelper(app: Application): DatabaseHelper {
        return DatabaseHelper(app)
    }

    @Provides @Singleton @IOSched fun provideIoScheduler(): Scheduler {
        return Schedulers.immediate()
    }

    @Provides @Singleton @UISched fun provideUiScheduler(): Scheduler {
        return Schedulers.immediate()
    }

    inner class MockWeatherApi(internal var forecast: Forecast) : WeatherApi {

        override fun getForecast(@Query("q") params: String,
                                 @Query("num_of_days") days: Int): Observable<Forecast> {
            return Observable.create(object : Observable.OnSubscribe<Forecast> {
                override fun call(subscriber: Subscriber<in Forecast>) {
                    subscriber.onNext(forecast)
                    subscriber.onCompleted()
                }
            })
        }
    }
}
