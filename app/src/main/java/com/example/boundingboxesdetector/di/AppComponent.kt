package com.example.boundingboxesdetector.di

import android.content.Context
import com.example.boundingboxesdetector.Application
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component
interface AppComponent {

    fun inject(application: Application)

    fun mainActivityComponentFactory() : MainActivityComponent.Factory

    @Component.Factory
    interface Factory{
        fun create(
           @BindsInstance context: Context
        ):AppComponent
    }

}