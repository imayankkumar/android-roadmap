package com.imayankkumar.roadmap.enumclasses

// Post section: "What is an enum class in Kotlin?"
// https://imayankkumar.com/blog/kotlin-enum-class/#what-is-an-enum-class-in-kotlin
//
// A fixed list of instances. Each constant is created once, and gets `name` and `ordinal` for free.

enum class SimpleStatus {
    PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED
}

fun sameObject(): Boolean = SimpleStatus.valueOf("PREPARING") === SimpleStatus.PREPARING
