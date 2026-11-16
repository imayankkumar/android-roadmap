package com.imayankkumar.roadmap.anyunitnothing

// Post section: "Why can a loading state be UiState<Nothing>?"
// https://imayankkumar.com/blog/kotlin-any-unit-nothing/#why-can-a-loading-state-be-uistatenothing
//
// Loading and Failed carry no data, so they use Nothing as the type argument.
// Because of `out T`, OrderState<Nothing> fits wherever OrderState<Order> is expected.

sealed interface OrderState<out T> {
    data object Loading : OrderState<Nothing>
    data class Ready<T>(val data: T) : OrderState<T>
    data class Failed(val message: String) : OrderState<Nothing>
}

/** One Loading object works for every screen, whatever data it shows. */
val orderScreen: OrderState<Order> = OrderState.Loading
val cartScreen: OrderState<List<CartItem>> = OrderState.Loading

fun title(state: OrderState<Order>): String = when (state) {
    OrderState.Loading -> "Loading your order"
    is OrderState.Ready -> "Order ${state.data.id}: ₹${state.data.total}"
    is OrderState.Failed -> "Something went wrong: ${state.message}"
}
