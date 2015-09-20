package io.dp.weather.app.activity

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment

import io.dp.weather.app.R

/**
 * A [PreferenceActivity] that presents a set of application settings. On handset devices,
 * settings are presented as a single list. On tablets, settings are split by category, with
 * category headers shown to the left of the list of settings.
 *
 * See [ Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings
   * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
public class SettingsActivity : PreferenceActivity() {


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setupSimplePreferencesScreen()
    }

    /**
     * Shows the simplified settings UI if the device configuration if the device configuration
     * dictates that a simplified, single-pane UI should be shown.
     */
    @SuppressWarnings("deprecation")
    private fun setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this) && !isSimplePreferences(this)
    }

    /**
     * This fragment shows general preferences only. It is used when the activity is showing a
     * two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class GeneralPreferenceFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
        }
    }

    companion object {

        /**
         * Determines whether to always show the simplified settings UI, where settings are presented in a
         * single list. When false, settings are shown as a master/detail two-pane view on tablets. When
         * true, a single pane is shown on tablets.
         */
        private val ALWAYS_SIMPLE_PREFS = false

        /**
         * Helper method to determine if the device has an extra-large screen. For example, 10" tablets
         * are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Determines whether the simplified settings UI should be shown. This is true if this is forced
         * via [.ALWAYS_SIMPLE_PREFS], or the device doesn't have newer APIs like [ ], or the device doesn't have an extra-large screen. In these cases, a
         * single-pane "simplified" settings UI should be shown.
         */
        private fun isSimplePreferences(context: Context): Boolean {
            return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context)
        }
    }
}
