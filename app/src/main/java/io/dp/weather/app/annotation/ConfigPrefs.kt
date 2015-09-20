package io.dp.weather.app.annotation

import java.lang.annotation.Documented
import java.lang.annotation.Retention

import javax.inject.Qualifier

import java.lang.annotation.RetentionPolicy.RUNTIME

/**
 * Created by dp on 11/10/14.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
annotation public class ConfigPrefs
