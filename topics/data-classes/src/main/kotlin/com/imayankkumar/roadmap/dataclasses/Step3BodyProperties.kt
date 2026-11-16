package com.imayankkumar.roadmap.dataclasses

// Post section: "Which properties count in equals()?"
// https://imayankkumar.com/blog/kotlin-data-class/#which-properties-count-in-equals
//
// Only properties in the primary constructor count. A property declared in the body is
// ignored by equals(), hashCode(), toString() and copy().

data class Order(val id: String, val items: List<CartItem>) {
    var isSeen: Boolean = false
}

fun seenAndUnseenAreEqual(): Boolean {
    val seen = Order("A1", emptyList()).apply { isSeen = true }
    val unseen = Order("A1", emptyList())
    return seen == unseen
}

/** copy() only knows constructor properties, so isSeen goes back to false. */
fun copyKeepsSeen(): Boolean {
    val seen = Order("A1", emptyList()).apply { isSeen = true }
    return seen.copy().isSeen
}
