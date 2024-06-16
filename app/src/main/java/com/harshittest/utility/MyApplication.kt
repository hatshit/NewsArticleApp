package com.harshittest.utility

import android.app.Application
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Set a global error handler for RxJava
        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) {
                // Log or handle the undeliverable exception
                e.printStackTrace()
            } else {
                // Other exceptions
                Thread.currentThread().uncaughtExceptionHandler
                    ?.uncaughtException(Thread.currentThread(), e)
            }
        }
    }
}