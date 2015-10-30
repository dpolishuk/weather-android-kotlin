package io.dp.weather.app.fragment

import android.location.Address
import android.location.Geocoder
import io.dp.weather.app.BuildConfig
import io.dp.weather.app.MockAppComponent
import io.dp.weather.app.TestApp
import io.dp.weather.app.activity.MockActivity
import io.dp.weather.app.db.DatabaseHelper
import io.dp.weather.app.db.table.Place
import io.dp.weather.app.event.AddPlaceEvent
import io.dp.weather.app.event.DeletePlaceEvent
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSQLiteConnection
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
import java.io.IOException
import java.util.*
import javax.inject.Inject

/**
 * Created by dp on 10/10/14.
 */
@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class,
        application = TestApp::class,
        manifest = "app/src/test/TestAndroidManifest.xml",
        resourceDir = "../main/res", sdk = 21)
class WeatherFragmentTest {
    @Inject lateinit  var geocoder: Geocoder

    @Inject lateinit  var databaseHelper: DatabaseHelper

    @Before @Throws(Exception::class)
    fun setUp() {
        val app = (RuntimeEnvironment.application as TestApp)

        (app.component as MockAppComponent?)!!.inject(this)
    }

    @After fun tearDown() {
        ShadowSQLiteConnection.reset()
    }

    @Test @Throws(Exception::class)
    fun testAddRemovePlaceFragment() {
        val f = WeatherFragment.newInstance()

        SupportFragmentTestUtil.startFragment(f, MockActivity::class.java)
        assertNotNull(f.adapter)

        val placeName = "Shanghai"

        val address = mock(Address::class.java)
        `when`(address.latitude).thenReturn(-1.0)
        `when`(address.longitude).thenReturn(-1.0)

        val addresses = ArrayList<Address>()
        addresses.add(address)

        try {
            `when`(geocoder.getFromLocationName(placeName, 1)).thenReturn(addresses)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        f.onAddPlace(AddPlaceEvent(placeName))

        val places = databaseHelper.getPlaceDao()!!.queryForEq(Place.NAME, placeName)

        assertEquals(1, places.size.toLong())
        assertEquals(placeName, places[0].name)

        assertEquals(5, f.adapter.itemCount.toLong())

        f.onDeletePlace(DeletePlaceEvent(1L))
        f.adapter.notifyDataSetChanged()

        val placeList = databaseHelper.getPlaceDao()!!.queryForAll()
        assertEquals(4, placeList.size.toLong())
        assertEquals(4, f.adapter.itemCount.toLong())
    }
}
