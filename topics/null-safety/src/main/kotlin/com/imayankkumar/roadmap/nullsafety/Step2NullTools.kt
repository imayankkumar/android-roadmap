package com.imayankkumar.roadmap.nullsafety

// Post section: "How do you use a value that might be null?"
// https://imayankkumar.com/blog/kotlin-null-safety/#how-do-you-use-a-value-that-might-be-null
// The four everyday tools, one function each.

/** Smart cast: after the check, order is Order, not Order?. */
fun orderTotalOrZero(order: Order?): Int {
    if (order != null) return order.total
    return 0
}

/** Safe call chain: stops at the first null and gives null. */
fun riderPhone(order: Order?): String? = order?.rider?.phone

/** Elvis gives a default when the left side is null. */
fun noteOrDefault(order: Order?): String = order?.deliveryNote ?: "No note"

/** Elvis with return: leave early, then the rest of the function has a non-null Order. */
fun checkoutSummary(orderId: String): String {
    val order = findOrder(orderId) ?: return "Order $orderId not found"
    return "Order ${order.id}: ₹${order.total}"
}

/** let runs the block only when the value is not null. */
fun riderLabel(order: Order?): String = order?.rider?.let { "${it.name} is on the way" } ?: "Finding a rider"
