package com.imayankkumar.roadmap.sealedvsenum

// Run main() from the IDE gutter to see every step's output in one place.

fun main() {
    println("Step 1: one state at a time")
    println("  fields allow: $impossibleScreen")
    println("  sealed: ${OrderUiState.Ready(listOf(CartItem("Lassi", 60)), 60)}")

    println("Step 2: when without else")
    println("  ${title(OrderUiState.Loading)} / ${title(OrderUiState.Ready(listOf(CartItem("Lassi", 60)), 60))}")

    println("Step 3: else hides a new state")
    val cancelled = TrackedOrderState.Cancelled("Restaurant closed")
    println("  with else: ${titleWithElse(cancelled)}, by name: ${titleByName(cancelled)}")

    println("Step 4: where subclasses live")
    println("  ${describe(PaymentResult.Paid("A1", "https://example.com/r/A1"))}, ${describe(Refunded("B2"))}")
    println("  run other-module's Main.kt for the subclass from another module")

    println("Step 5: sealed interface")
    println("  ${handle(ItemAdded("Lassi"))}, ${track(ItemAdded("Lassi"))}, ${handle(SortOrder.PRICE)}")
}
