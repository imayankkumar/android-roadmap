package com.imayankkumar.roadmap.dataclasses

// Post section: "Why does copy() warn about a private constructor?"
// https://imayankkumar.com/blog/kotlin-data-class/#why-does-copy-warn-about-a-private-constructor
//
// A private constructor plus a factory is a common way to check values.
// But the generated copy() is public, so it can skip the check.

/** @ExposedCopyVisibility keeps today's behavior (public copy) and silences the warning. */
@ExposedCopyVisibility
data class Coupon private constructor(val percent: Int) {
    companion object {
        fun of(percent: Int): Coupon {
            require(percent in 1..50) { "Discount must be 1 to 50%, was $percent" }
            return Coupon(percent)
        }
    }
}

/** copy() goes around the check in of(). */
fun bypassCheck(): Coupon = Coupon.of(10).copy(percent = 90)

/** @ConsistentCopyVisibility makes copy() private, like the constructor. Outside code can't call it. */
@ConsistentCopyVisibility
data class SafeCoupon private constructor(val percent: Int) {
    companion object {
        fun of(percent: Int): SafeCoupon {
            require(percent in 1..50) { "Discount must be 1 to 50%, was $percent" }
            return SafeCoupon(percent)
        }
    }
}
