package com.imayankkumar.roadmap.innervsnestedclasses

// Post section: "How can an inner class leak an Activity?"
// https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/#how-can-an-inner-class-leak-an-activity
//
// This module is plain Kotlin, not Android, so TrackingScreen stands in for an Activity:
// onCreate/onStart/onStop/onDestroy are called by hand, like the system would on rotation.
// The point is the same: OrderUpdates lives as long as the process, and an inner listener
// holds its screen through this$0.

fun interface StatusListener {
    fun onStatus(status: String)
}

object OrderUpdates {
    private val listeners = mutableListOf<StatusListener>()
    fun add(listener: StatusListener) { listeners += listener }
    fun remove(listener: StatusListener) { listeners -= listener }
    fun send(status: String) = listeners.forEach { it.onStatus(status) }
    fun listeners(): List<StatusListener> = listeners.toList()
    fun clear() = listeners.clear()
}

/** Stands in for an Activity: it holds its "views" until it is destroyed. */
abstract class TrackingScreen {
    var statusText: String = ""
    var isDestroyed = false
        private set
    open fun onCreate() {}
    open fun onStart() {}
    open fun onStop() {}
    fun onDestroy() { isDestroyed = true }
}

/** Almost right: added in onCreate, never removed. */
class TrackingScreenAlmostRight : TrackingScreen() {
    inner class StatusUpdater : StatusListener {
        override fun onStatus(status: String) { statusText = status }
    }

    override fun onCreate() { OrderUpdates.add(StatusUpdater()) }
}

/** Right: still inner, but it lives only while the screen is visible. */
class TrackingScreenRight : TrackingScreen() {
    inner class StatusUpdater : StatusListener {
        override fun onStatus(status: String) { statusText = status }
    }

    private val updater = StatusUpdater()

    override fun onStart() { OrderUpdates.add(updater) }
    override fun onStop() { OrderUpdates.remove(updater) }
}

/** Rotate the "phone": the old screen stops and is destroyed, a new one is created and started. */
fun rotate(old: TrackingScreen, create: () -> TrackingScreen): TrackingScreen {
    old.onStop()
    old.onDestroy()
    return create().apply { onCreate(); onStart() }
}

/** Reads the hidden this$0 field, the way a heap dump shows who holds what. */
fun outerOf(listener: StatusListener): Any? =
    listener.javaClass.declaredFields
        .firstOrNull { it.name == "this$0" }
        ?.apply { isAccessible = true }
        ?.get(listener)
