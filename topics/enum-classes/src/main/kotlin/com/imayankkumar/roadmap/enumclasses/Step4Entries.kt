package com.imayankkumar.roadmap.enumclasses

import kotlin.enums.enumEntries

// Post section: "Should you use entries or values()?"
// https://imayankkumar.com/blog/kotlin-enum-class/#should-you-use-entries-or-values
//
// values() makes a new array on every call. entries is one read-only List.

fun valuesIsNewEachTime(): Boolean = OrderStatus.values() !== OrderStatus.values()

fun entriesIsSameEachTime(): Boolean = OrderStatus.entries === OrderStatus.entries

/** For generic code, where you only know the enum as T. */
inline fun <reified T : Enum<T>> names(): List<String> = enumEntries<T>().map { it.name }
