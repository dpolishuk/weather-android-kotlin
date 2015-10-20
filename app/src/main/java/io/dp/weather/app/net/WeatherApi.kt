package io.dp.weather.app.net

import io.dp.weather.app.net.dto.Forecast
import retrofit.http.GET
import retrofit.http.Query
import rx.Observable

/**
 * Created by dp on 08/10/14.
 */
interface WeatherApi {

    @GET("/weather.ashx")
    fun getForecast(@Query("q") params: String, @Query("num_of_days") days: Int): Observable<Forecast>
}
