package com.imayankkumar.roadmap.dataclasses

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// Post section: "Is copy() a deep copy?"
// https://imayankkumar.com/blog/kotlin-data-class/#is-copy-a-deep-copy
//
// No. copy() copies references, so the old and new state share the same list.
// StateFlow only emits when the new value is not equal to the old one.

/** Almost right: a mutable list inside the state. */
data class CartStateAlmostRight(val items: MutableList<CartItem>)

class CartViewModelAlmostRight {
    private val _state = MutableStateFlow(CartStateAlmostRight(mutableListOf()))
    val state: StateFlow<CartStateAlmostRight> = _state

    fun add(item: CartItem) {
        val current = _state.value
        current.items.add(item)          // changes the list the OLD state also points to
        _state.value = current.copy()    // equal to the old state, so StateFlow skips it
    }
}

/** Right: a read-only list, and every change makes a new list. */
data class CartState(val items: List<CartItem>)

class CartViewModel {
    private val _state = MutableStateFlow(CartState(emptyList()))
    val state: StateFlow<CartState> = _state

    fun add(item: CartItem) {
        _state.update { it.copy(items = it.items + item) }
    }
}
