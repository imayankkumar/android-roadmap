package com.imayankkumar.roadmap.sealedvsenum

// Post section: "Sealed class or sealed interface?"
// https://imayankkumar.com/blog/kotlin-sealed-class/#sealed-class-or-sealed-interface
//
// A type can implement two sealed interfaces, so it can be in two closed lists.
// An enum can implement a sealed interface, but it can't extend a sealed class.

sealed interface CartEvent
sealed interface AnalyticsEvent

data class ItemAdded(val name: String) : CartEvent, AnalyticsEvent
data class ItemRemoved(val name: String) : CartEvent
data class ScreenOpened(val screen: String) : AnalyticsEvent

enum class SortOrder : CartEvent { PRICE, RATING }

fun handle(event: CartEvent): String = when (event) {
    is ItemAdded -> "add ${event.name}"
    is ItemRemoved -> "remove ${event.name}"
    SortOrder.PRICE -> "sort by price"
    SortOrder.RATING -> "sort by rating"
}

fun track(event: AnalyticsEvent): String = when (event) {
    is ItemAdded -> "item_added"
    is ScreenOpened -> "screen_opened"
}
