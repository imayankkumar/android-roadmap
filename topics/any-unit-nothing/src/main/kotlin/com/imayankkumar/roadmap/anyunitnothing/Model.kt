package com.imayankkumar.roadmap.anyunitnothing

// The same small checkout model the post uses.

data class CartItem(val name: String, val price: Int)

data class Order(val id: String, val total: Int)

val cartItems = mutableListOf(CartItem("Paneer roll", 180), CartItem("Lassi", 60))

private val orders = mapOf("A1" to Order("A1", 240))

/** Returns null when there is no order with this id. */
fun findOrder(orderId: String): Order? = orders[orderId]
