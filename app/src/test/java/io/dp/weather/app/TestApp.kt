package io.dp.weather.app

import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

/**
 * Created by deepol on 10/09/15.
 */
class TestApp : WeatherApp(), TestLifecycleApplication {
    override fun createComponent(): AppComponent {
        return DaggerMockAppComponent.builder().mockAppModule(MockAppModule(this)).build()
    }

    override fun beforeTest(method: Method) {

    }

    override fun prepareTest(test: Any) {

    }

    override fun afterTest(method: Method) {

    }
}
