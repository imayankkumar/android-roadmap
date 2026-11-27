package com.imayankkumar.roadmap.objects

import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

// Post section: "When is it created, and is it thread-safe?"
// https://imayankkumar.com/blog/kotlin-object/#when-is-it-created-and-is-it-thread-safe
//
// The JVM runs an object's static block once, the first time code uses it.
// Properties and init blocks run in the order you wrote them.

val creationLog: MutableList<String> = Collections.synchronizedList(mutableListOf())

private fun log(step: String): String {
    creationLog += step
    return step
}

object AppConfig {
    val first = log("AppConfig: first property")

    init {
        log("AppConfig: init block")
    }

    val second = log("AppConfig: second property")
}

// Reading a const val doesn't create the object: the compiler copies "2.4" into the caller.
object BuildInfo {
    const val VERSION = "2.4"
    val createdAt = log("BuildInfo: created")
}

// Creation is thread-safe: many threads, one instance, init runs once.
val registryCreations = AtomicInteger()

object Registry {
    init {
        registryCreations.incrementAndGet()
    }
}

// But a var inside an object is not thread-safe. count can lose updates;
// safeCount can't, because AtomicInteger makes each increment one step.
object Hits {
    var count = 0
    val safeCount = AtomicInteger()
}
