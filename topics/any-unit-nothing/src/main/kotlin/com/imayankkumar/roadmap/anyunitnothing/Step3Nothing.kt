package com.imayankkumar.roadmap.anyunitnothing

// Post section: "What is Nothing, and why would a function return it?"
// https://imayankkumar.com/blog/kotlin-any-unit-nothing/#what-is-nothing-and-why-would-a-function-return-it
//
// Nothing has no values. A function that returns Nothing never returns normally: it always throws.
// Because Nothing is a subtype of every type, the compiler knows the code after it can't run.

/** Declared as Nothing, so the compiler knows this call never comes back. */
fun stopOrder(reason: String): Nothing = throw IllegalStateException(reason)

/** After `?: stopOrder(...)`, order is Order, not Order?. No !! needed. */
fun orderTotal(orderId: String): Int {
    val order = findOrder(orderId) ?: stopOrder("Order $orderId not found")
    return order.total
}

/** error() from the standard library also returns Nothing. */
fun orderTotalWithError(orderId: String): Int {
    val order: Order = findOrder(orderId) ?: error("Order $orderId not found")
    return order.total
}

/** `val x = null` has the type Nothing?, so it fits any nullable type. */
fun nullFitsAnywhere(): Pair<String?, Order?> {
    val x = null
    val name: String? = x
    val order: Order? = x
    return name to order
}
