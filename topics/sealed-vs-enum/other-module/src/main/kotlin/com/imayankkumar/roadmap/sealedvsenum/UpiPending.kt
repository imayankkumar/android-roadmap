package com.imayankkumar.roadmap.sealedvsenum

// Post section: "What about indirect subclasses?"
// https://imayankkumar.com/blog/kotlin-sealed-class/#what-about-indirect-subclasses
//
// This is a different module. It can't extend PaymentResult directly (see wont-compile/),
// but Pending is open, so extending Pending is allowed.

class UpiPending(orderId: String, val upiId: String) : PaymentResult.Pending(orderId)

fun main() {
    // describe() lives in the other module. Its `is Pending` branch also catches UpiPending.
    println(describe(UpiPending("C3", "mayank@upi")))
}
