package com.imayankkumar.roadmap.anyunitnothing

// Run main() from the IDE gutter to see every step's output in one place.

fun main() {
    println("Step 1: Any")
    println("  " + logEvent("checkout", "items" to 3, "coupon" to "SAVE10", "note" to null))

    println("Step 2: Unit")
    val result = clearCart()
    println("  clearCart() returned $result")

    println("Step 3: Nothing")
    println("  total for A1 = ${orderTotal("A1")}")
    runCatching { orderTotal("Z9") }.onFailure { println("  $it") }

    println("Step 4: Loading state")
    println("  ${title(orderScreen)} | ${title(OrderState.Ready(Order("A1", 240)))}")
}
