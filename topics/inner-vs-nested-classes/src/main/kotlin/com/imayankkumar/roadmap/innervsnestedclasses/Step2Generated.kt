package com.imayankkumar.roadmap.innervsnestedclasses

// Post section: "What does the compiler generate?"
// https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/#what-does-the-compiler-generate
//
// Open decompiled/.../OrderTracker.java and Bill.java to see:
// - the nested class is static, the inner class has a `this$0` field
// - private members used from an inner class get `access$...` helper methods

class Bill(private var total: Int) {

    inner class AddItem {
        fun add(price: Int) {
            total += price          // private in Bill: uses access$getTotal$p / access$setTotal$p
        }
    }

    /** A nested class can read private members too, but only through an instance. */
    class Peek {
        fun totalOf(bill: Bill) = bill.total
    }

    /** A lambda only keeps `this` (the Bill) if its body uses something from Bill. */
    fun lambdaWithoutBill(): () -> String = { "no Bill here" }
    fun lambdaWithBill(): () -> String = { "total $total" }
}
