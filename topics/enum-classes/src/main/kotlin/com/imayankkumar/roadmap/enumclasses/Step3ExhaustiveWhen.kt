package com.imayankkumar.roadmap.enumclasses

// Post section: "Why doesn't when need an else branch?"
// https://imayankkumar.com/blog/kotlin-enum-class/#why-doesnt-when-need-an-else-branch
//
// The compiler knows every constant. Add CANCELLED to OrderStatus and this stops compiling.

fun label(status: OrderStatus): String = when (status) {
    OrderStatus.PLACED -> "Order placed"
    OrderStatus.PREPARING -> "Being prepared"
    OrderStatus.OUT_FOR_DELIVERY -> "On the way"
    OrderStatus.DELIVERED -> "Delivered"
}
