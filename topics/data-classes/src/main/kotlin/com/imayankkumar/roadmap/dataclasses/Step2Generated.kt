package com.imayankkumar.roadmap.dataclasses

// Post section: "What does the compiler generate for a data class?"
// https://imayankkumar.com/blog/kotlin-data-class/#what-does-the-compiler-generate-for-a-data-class
//
// For every property in the primary constructor the compiler writes equals(), hashCode(),
// toString(), componentN() and copy(). Open decompiled/.../CartItem.java to see all of them.

/** toString() lists every constructor property. */
fun printedItem(): String = CartItem("Paneer roll", 180, 1).toString()

/** component1(), component2(), component3() make destructuring work. */
fun lineTotal(item: CartItem): Int {
    val (_, price, quantity) = item
    return price * quantity
}

/** copy() makes a new object and changes only the values you name. */
fun withOneMore(item: CartItem): CartItem = item.copy(quantity = item.quantity + 1)
