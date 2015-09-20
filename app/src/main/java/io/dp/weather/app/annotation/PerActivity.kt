package io.dp.weather.app.annotation

import java.lang.annotation.Retention
import javax.inject.Scope

import java.lang.annotation.RetentionPolicy.RUNTIME

/**
 * A scoping annotation to permit objects whose lifetime should
 * conform to the life of the activity to be memorized in the
 * correct component.
 */
@Scope @Retention(RUNTIME) annotation public class PerActivity
