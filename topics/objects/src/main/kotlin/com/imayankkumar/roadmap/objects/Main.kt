package com.imayankkumar.roadmap.objects

import kotlin.concurrent.thread

// Run main() from the IDE gutter to see every step's output in one place.

fun main() {
    println("Step 1: object declaration")
    println("  class: ${classLosesTheEmail()}")
    println("  object: ${objectKeepsTheEmail()}")

    println("Step 2: what the compiler makes")
    println("  logged in: ${checkLogin()}")

    println("Step 3: when it is created")
    println("  read const: ${BuildInfo.VERSION}, log: $creationLog")
    println("  use AppConfig: ${AppConfig.second}")
    creationLog.forEach { println("    $it") }
    val threads = List(8) { thread { repeat(10_000) { Hits.count++; Hits.safeCount.incrementAndGet() } } }
    threads.forEach { it.join() }
    println("  var count: ${Hits.count} (expected 80000), AtomicInteger: ${Hits.safeCount.get()}")

    println("Step 4: object expression")
    println("  makeListener() === makeListener(): ${makeListener() === makeListener()}")
    val button = LoginButton()
    addThenRemoveWithTwoObjects(button)
    println("  two object expressions, listeners left: ${button.listenerCount}")
    val screenButton = LoginButton()
    val screen = LoginScreen(screenButton)
    screen.onStart()
    screen.onStop()
    println("  one kept reference, listeners left: ${screenButton.listenerCount}")

    println("Step 5: data object")
    println("  ${AuthState.LoggedOut}")
    println("  ${AuthState.LoggedIn("hello@imayankkumar.com")}")
    println("  ${PlainAuthState.LoggedOut}")
}
