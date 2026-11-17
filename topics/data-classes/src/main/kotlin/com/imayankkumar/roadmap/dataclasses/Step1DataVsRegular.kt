package com.imayankkumar.roadmap.dataclasses

// Post section: "What is a data class in Kotlin?"
// https://imayankkumar.com/blog/kotlin-data-class/#what-is-a-data-class-in-kotlin
//
// Same constructor, one difference: the `data` keyword.
// A data class compares by value. A regular class compares by identity (same object or not).

data class CartItem(val name: String, val price: Int, val quantity: Int)

class PlainCartItem(val name: String, val price: Int, val quantity: Int)

fun sameDataItems(): Boolean = CartItem("Paneer roll", 180, 1) == CartItem("Paneer roll", 180, 1)

fun samePlainItems(): Boolean = PlainCartItem("Paneer roll", 180, 1) == PlainCartItem("Paneer roll", 180, 1)

/** A set keeps one of two equal values, so equality decides the size. */
fun dataSetSize(): Int = setOf(CartItem("Lassi", 60, 2), CartItem("Lassi", 60, 2)).size

fun plainSetSize(): Int = setOf(PlainCartItem("Lassi", 60, 2), PlainCartItem("Lassi", 60, 2)).size
