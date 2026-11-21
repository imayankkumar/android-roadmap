package com.imayankkumar.roadmap.enumclasses

// Post section: "How do you add values and functions to an enum?"
// https://imayankkumar.com/blog/kotlin-enum-class/#how-do-you-add-values-and-functions-to-an-enum
//
// An enum can have a constructor, properties and functions. A constant can have its own body.

enum class OrderStatus(val code: String) {
    PLACED("placed"),
    PREPARING("preparing"),
    OUT_FOR_DELIVERY("out_for_delivery"),
    DELIVERED("delivered");

    val isActive: Boolean
        get() = this != DELIVERED

    companion object {
        /** Used in Step 5: find a constant by a code you control, or null if the code is unknown. */
        fun fromCode(code: String?): OrderStatus? = entries.firstOrNull { it.code == code }
    }
}

enum class DeliverySpeed {
    STANDARD {
        override fun deliveryCharge(orderTotal: Int) = if (orderTotal >= 500) 0 else 30
    },
    EXPRESS {
        override fun deliveryCharge(orderTotal: Int) = 60
    };

    abstract fun deliveryCharge(orderTotal: Int): Int
}
