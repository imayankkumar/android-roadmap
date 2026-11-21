package com.imayankkumar.roadmap.sealedvsenum

// Post section: "What is a sealed class in Kotlin?"
// https://imayankkumar.com/blog/kotlin-sealed-class/#what-is-a-sealed-class-in-kotlin
//
// A sealed type has a fixed list of subtypes. The screen is in exactly one state,
// so "loading and failed at the same time" can't be written.

/** Almost right: three fields can be set in a mix that makes no sense. */
data class OrderScreenFields(
    val isLoading: Boolean,
    val error: String?,
    val items: List<CartItem>?,
)

/** Nothing stops this from compiling: a spinner and an error at the same time. */
val impossibleScreen = OrderScreenFields(isLoading = true, error = "Timeout", items = null)

/** Right: one state at a time. */
sealed interface OrderUiState {
    data object Loading : OrderUiState
    data class Ready(val items: List<CartItem>, val total: Int) : OrderUiState
    data class Failed(val message: String) : OrderUiState
}

/** A sealed class is abstract, and its constructor is protected by default. */
sealed class Discount(val percent: Int) {
    class Coupon(val code: String, percent: Int) : Discount(percent)
    class FirstOrder : Discount(20)
}
