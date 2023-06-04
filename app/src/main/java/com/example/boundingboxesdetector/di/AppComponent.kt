package com.example.boundingboxesdetector.di

import com.example.boundingboxesdetector.Application
import dagger.Component

@ApplicationScope
@Component
interface AppComponent {

    fun inject(application: Application)

    fun mainActivityComponentFactory() : MainActivityComponent.Factory

}