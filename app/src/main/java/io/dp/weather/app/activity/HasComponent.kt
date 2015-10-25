package io.dp.weather.app.activity

interface HasComponent<T> {

    fun createComponent(): T

    fun getComponent(): T
}
