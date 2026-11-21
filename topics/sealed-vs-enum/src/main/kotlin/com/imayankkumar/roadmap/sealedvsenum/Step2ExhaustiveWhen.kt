package com.imayankkumar.roadmap.sealedvsenum

// Post section: "Why doesn't when need an else branch?"
// https://imayankkumar.com/blog/kotlin-sealed-class/#why-doesnt-when-need-an-else-branch
//
// The compiler knows every subtype, so it checks that `when` covers all of them.
// Inside each `is` branch, `state` is smart cast to that subtype.

fun title(state: OrderUiState): String = when (state) {
    OrderUiState.Loading -> "Loading"
    is OrderUiState.Ready -> "Your order (${state.items.size} items)"
    is OrderUiState.Failed -> "Something went wrong"
}
