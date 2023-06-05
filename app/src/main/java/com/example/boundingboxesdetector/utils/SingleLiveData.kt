package com.example.boundingboxesdetector.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/** Оболочка для MutableLiveData, чтобы отображать данные 1 раз при их появлении **/
class SingleLiveData<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    override fun setValue(value: T) {
        pending.set(true)
        super.setValue(value)
    }

    override fun postValue(value: T) {
        pending.set(true)
        super.postValue(value)
    }
}