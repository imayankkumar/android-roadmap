package com.imayankkumar.roadmap.nullsafety

// A small food-delivery model used by every step, the same one the post uses.

data class Rider(val name: String, val phone: String?)

data class Order(
    val id: String,
    val total: Int,
    val rider: Rider?,          // no rider yet while the restaurant is cooking
    val deliveryNote: String?,  // most customers leave no note
)

private val orders = mapOf(
    "A1" to Order(id = "A1", total = 450, rider = Rider("Ravi", phone = "98xxxxxx01"), deliveryNote = "Ring twice"),
    "B2" to Order(id = "B2", total = 220, rider = null, deliveryNote = null),
)

/** Returns null when there is no order with this id. */
fun findOrder(orderId: String): Order? = orders[orderId]
