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

/**
 * Created by dp on 07/10/14.
 */

@Module
public class ActivityModule(private val activity: FragmentActivity) {

    @Provides
    @PerActivity
    public fun provideActivity(): FragmentActivity {
        return activity
    }

    @Provides
    @PerActivity
    public fun provideDatabaseHelper(): DatabaseHelper {
        return OpenHelperManager.getHelper(activity, DatabaseHelper::class.java)
    }

    @Provides
    @ConfigPrefs
    @PerActivity
    public fun provideConfigPrefs(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(activity)
    }

    @Provides
    @CachePrefs
    @PerActivity
    public fun provideCachePrefs(): SharedPreferences {
        return activity.getSharedPreferences("cachePrefs", Context.MODE_PRIVATE)
    }
}
