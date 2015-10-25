package io.dp.weather.app.activity

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.app.FragmentActivity
import com.j256.ormlite.android.apptools.OpenHelperManager
import dagger.Module
import dagger.Provides
import io.dp.weather.app.annotation.CachePrefs
import io.dp.weather.app.annotation.ConfigPrefs
import io.dp.weather.app.annotation.PerActivity
import io.dp.weather.app.db.DatabaseHelper

@Module
class ActivityModule(private val activity: FragmentActivity) {

    @Provides
    @PerActivity
    fun provideActivity(): FragmentActivity {
        return activity
    }

    @Provides
    @PerActivity
    fun provideDatabaseHelper(): DatabaseHelper {
        return OpenHelperManager.getHelper(activity, DatabaseHelper::class.java)
    }

    @Provides
    @ConfigPrefs
    @PerActivity
    fun provideConfigPrefs(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(activity)
    }

    @Provides
    @CachePrefs
    @PerActivity
    fun provideCachePrefs(): SharedPreferences {
        return activity.getSharedPreferences("cachePrefs", Context.MODE_PRIVATE)
    }
}
