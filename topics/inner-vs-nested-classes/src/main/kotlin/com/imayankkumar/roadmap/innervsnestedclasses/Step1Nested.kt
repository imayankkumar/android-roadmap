package com.imayankkumar.roadmap.innervsnestedclasses

// Post sections: "What is a nested class in Kotlin?" and "What changes when you add inner?"
// https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/#what-is-a-nested-class-in-kotlin
// https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/#what-changes-when-you-add-inner
//
// A nested class has no link to an OrderTracker. An inner class belongs to one OrderTracker.

class OrderTracker(val orderId: String) {

    /** Nested: created without any OrderTracker. It can't read orderId. */
    class DeliveryStop(val name: String, val etaMinutes: Int)

    /** Inner: created from an OrderTracker, and can read its members. */
    inner class StatusLabel {
        fun text() = "Order $orderId"
        fun tracker() = this@OrderTracker
    }
}

fun firstStop(): OrderTracker.DeliveryStop = OrderTracker.DeliveryStop("Restaurant", 12)

fun labelFor(tracker: OrderTracker): OrderTracker.StatusLabel = tracker.StatusLabel()
