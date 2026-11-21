package com.imayankkumar.roadmap.enumclasses

// Post section: "Enum or sealed class?"
// https://imayankkumar.com/blog/kotlin-enum-class/#enum-or-sealed-class
//
// Enum: every option has the same shape, with values fixed when you write the code.
// Sealed: each option carries its own data, made at runtime.

enum class SortOrder(val label: String) { PRICE("Price"), RATING("Rating") }

sealed interface CheckoutState {
    data object Loading : CheckoutState
    data class Ready(val total: Int) : CheckoutState
    data class Failed(val message: String) : CheckoutState   // the message only exists at runtime
}

fun describe(state: CheckoutState): String = when (state) {
    CheckoutState.Loading -> "Loading"
    is CheckoutState.Ready -> "Pay ${state.total}"
    is CheckoutState.Failed -> "Error: ${state.message}"
}
