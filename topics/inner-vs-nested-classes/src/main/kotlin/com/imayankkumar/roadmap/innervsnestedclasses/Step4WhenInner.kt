package com.imayankkumar.roadmap.innervsnestedclasses

// Post section: "When should you use inner?"
// https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/#when-should-you-use-inner
//
// Inner: a small helper that lives and dies with one outer object (like a ViewHolder that
// calls its adapter). Nested: everything else. If you need one value, pass the value.

class MenuAdapter(private val onClick: (String) -> Unit) {

    /** Inner is fine here: holders are created and dropped together with the adapter. */
    inner class ItemHolder(private val name: String) {
        fun click() = onClick(name)
    }

    fun holderFor(name: String) = ItemHolder(name)
}

class OrderSummary(val orderId: String, val lines: List<String>) {

    /** Nested, and it gets only the value it needs, not the whole OrderSummary. */
    class Header(private val orderId: String) {
        fun text() = "Order $orderId"
    }

    fun header() = Header(orderId)
}
