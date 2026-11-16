package com.imayankkumar.roadmap.nullsafety

// Post section: "Why is !! risky?"
//
// !! tells the compiler "trust me". If you're wrong, the crash moves from build time to runtime.

/** Almost right: works until findOrder returns null, then throws NullPointerException. */
fun orderTitleAlmostRight(orderId: String): String {
    val order = findOrder(orderId)
    return "Order ${order!!.id} · ₹${order.total}"
}

/** Right: the missing case is handled where it happens. */
fun orderTitleRight(orderId: String): String {
    val order = findOrder(orderId) ?: return "Order not found"
    return "Order ${order.id} · ₹${order.total}"
}

/** When null really is a bug, fail with a message that explains it, not a bare NPE. */
fun orderForRefund(orderId: String): Order =
    requireNotNull(findOrder(orderId)) { "Refund asked for unknown order $orderId" }
