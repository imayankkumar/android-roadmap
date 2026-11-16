package com.imayankkumar.roadmap.nullsafety

// Post section: "How does Kotlin stop null crashes?"
// https://imayankkumar.com/blog/kotlin-null-safety/#how-does-kotlin-stop-null-crashes
//
// String can never hold null. String? can. The compiler checks this at build time,
// so the crash you'd get in Java becomes a build error in Kotlin.
// See wont-compile/Step1*.kt.txt for the two errors the post shows.

fun restaurantName(): String = "Spice Route"   // can never be null

fun deliveryNoteLength(order: Order): Int {
    val note: String? = order.deliveryNote     // may be null
    // note.length would not compile here. A null check makes it a smart cast:
    if (note != null) {
        return note.length                      // note is String inside this block
    }
    return 0
}
