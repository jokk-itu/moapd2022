package dk.itu.moapd.scootersharing.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class LifecycleOwnerUtil : LifecycleOwner {

    private val lifeCycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    fun start() {
        lifeCycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun stop() {
        lifeCycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun getLifecycle(): Lifecycle = lifeCycleRegistry
}