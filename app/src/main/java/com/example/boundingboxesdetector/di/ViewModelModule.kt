package com.example.boundingboxesdetector.di

import androidx.lifecycle.ViewModel
import com.example.boundingboxesdetector.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel
}