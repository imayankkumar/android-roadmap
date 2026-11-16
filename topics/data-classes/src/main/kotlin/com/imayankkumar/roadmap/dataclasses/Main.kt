package com.imayankkumar.roadmap.dataclasses

// Run main() from the IDE gutter to see every step's output in one place.

fun main() {
    println("Step 1: data class vs regular class")
    println("  equal? data = ${sameDataItems()}, plain = ${samePlainItems()}")
    println("  set size: data = ${dataSetSize()}, plain = ${plainSetSize()}")

    println("Step 2: what the compiler generates")
    println("  ${printedItem()}")
    println("  line total = ${lineTotal(CartItem("Lassi", 60, 2))}, one more = ${withOneMore(CartItem("Lassi", 60, 2))}")

    println("Step 3: body properties")
    println("  seen == unseen? ${seenAndUnseenAreEqual()}, copy keeps isSeen? ${copyKeepsSeen()}")

    println("Step 4: shallow copy")
    println("  run Step4ShallowCopyTest in DataClassTest.kt to watch StateFlow skip the update")

    println("Step 5: copy and a private constructor")
    println("  ${bypassCheck()} (the factory allows at most 50)")
}
