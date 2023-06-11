package com.example.boundingboxesdetector

import android.app.Application
import com.example.boundingboxesdetector.di.AppComponent
import com.example.boundingboxesdetector.di.DaggerAppComponent

class Application : Application() {

    val component: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
    }
}