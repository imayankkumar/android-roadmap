package com.imayankkumar.roadmap.nullsafety

// Run main() from the IDE gutter to see every step's output in one place.

fun main() {
    println("Step 1: nullable types")
    println("  ${restaurantName()}, note length for A1 = ${deliveryNoteLength(findOrder("A1")!!)}")

    println("Step 2: the four null tools")
    println("  rider phone A1 = ${riderPhone(findOrder("A1"))}, B2 = ${riderPhone(findOrder("B2"))}")
    println("  note B2 = ${noteOrDefault(findOrder("B2"))}")
    println("  ${checkoutSummary("A1")} | ${checkoutSummary("Z9")}")
    println("  ${riderLabel(findOrder("A1"))} | ${riderLabel(findOrder("B2"))}")

    println("Step 3: !!")
    println("  right: ${orderTitleRight("Z9")}")
    runCatching { orderTitleAlmostRight("Z9") }.onFailure { println("  almost right: $it") }

    println("Step 4: Java interop")
    println("  ${couponOrNone("B2")} | ${couponFromAnnotated("A1")}")
    runCatching { couponCrashes("B2") }.onFailure { println("  crash: $it") }
}
