package com.imayankkumar.roadmap.innervsnestedclasses

// Run main() from the IDE gutter to see every step's output in one place.

fun main() {
    println("Step 1: nested vs inner")
    val tracker = OrderTracker("A12")
    println("  nested: ${firstStop().name}, inner: ${labelFor(tracker).text()}")

    println("Step 2: what the compiler generates")
    val bill = Bill(100)
    bill.AddItem().add(60)
    println("  total via nested Peek = ${Bill.Peek().totalOf(bill)}")

    println("Step 3: rotate a screen that never removes its listener")
    OrderUpdates.clear()
    val first = TrackingScreenAlmostRight().apply { onCreate(); onStart() }
    rotate(first) { TrackingScreenAlmostRight() }
    val held = OrderUpdates.listeners().map { outerOf(it) as TrackingScreen }
    println("  listeners = ${held.size}, destroyed screens still held = ${held.count { it.isDestroyed }}")

    println("Step 4: when to use inner")
    MenuAdapter { println("  clicked $it") }.holderFor("Paneer roll").click()
}
