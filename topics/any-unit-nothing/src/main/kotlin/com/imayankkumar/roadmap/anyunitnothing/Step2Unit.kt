package com.imayankkumar.roadmap.anyunitnothing

// Post section: "What is Unit, and how is it different from void?"
// https://imayankkumar.com/blog/kotlin-any-unit-nothing/#what-is-unit-and-how-is-it-different-from-void
//
// Unit is a real type with exactly one value, also called Unit.
// A function with no return type returns Unit, and the compiler turns it into a Java void method.

/** Returns Unit. In bytecode this is a plain void method. */
fun clearCart() {
    cartItems.clear()
}

/**
 * When Unit is used as a value, for example as a generic result, the compiler
 * hands back the single Unit object. See this in the decompiled Java.
 */
fun <T> timed(block: () -> T): T = block()

fun clearCartTimed(): Unit = timed { clearCart() }
