package com.imayankkumar.roadmap.sealedvsenum

// Post section: "Why is else almost right?"
// https://imayankkumar.com/blog/kotlin-sealed-class/#why-is-else-almost-right
//
// Same states as Step 1, plus Cancelled. The version with `else` still compiles,
// and a cancelled order quietly shows "Loading".

sealed interface TrackedOrderState {
    data object Loading : TrackedOrderState
    data class Ready(val items: List<CartItem>) : TrackedOrderState
    data class Failed(val message: String) : TrackedOrderState
    data class Cancelled(val reason: String) : TrackedOrderState   // added later
}

/** Almost right: compiles today, hides tomorrow's state. */
fun titleWithElse(state: TrackedOrderState): String = when (state) {
    is TrackedOrderState.Ready -> "Your order"
    is TrackedOrderState.Failed -> "Something went wrong"
    else -> "Loading"
}

/** Right: every state by name, no else. Without the Cancelled line, this doesn't compile. */
fun titleByName(state: TrackedOrderState): String = when (state) {
    TrackedOrderState.Loading -> "Loading"
    is TrackedOrderState.Ready -> "Your order"
    is TrackedOrderState.Failed -> "Something went wrong"
    is TrackedOrderState.Cancelled -> "Cancelled: ${state.reason}"
}
