package com.example.boundingboxesdetector.di

import androidx.activity.result.ActivityResultRegistry
import com.example.boundingboxesdetector.MainActivity
import com.example.boundingboxesdetector.utils.CameraUtil
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [ViewModelModule::class])
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(cameraUtil: CameraUtil)

    @Subcomponent.Factory
    interface Factory{

        fun create(
            @BindsInstance cameraUtil: CameraUtil,
            @BindsInstance resultRegistry: ActivityResultRegistry,
        ):MainActivityComponent
    }
}