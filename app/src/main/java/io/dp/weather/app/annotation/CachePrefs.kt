package io.dp.weather.app.annotation

import java.lang.annotation.Documented
import kotlin.annotation.Retention

import javax.inject.Qualifier

import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * Created by dp on 11/10/14.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
annotation public class CachePrefs
