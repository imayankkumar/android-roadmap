package com.imayankkumar.roadmap.sealedvsenum

// Post sections: "Where can the subclasses of a sealed class live?" and "What about indirect subclasses?"
// https://imayankkumar.com/blog/kotlin-sealed-class/#where-can-the-subclasses-of-a-sealed-class-live
// https://imayankkumar.com/blog/kotlin-sealed-class/#what-about-indirect-subclasses
//
// Direct subclasses must be in the same module and the same package. They can be in any file:
// Refunded is in Step4OtherFile.kt. Pending is open, so other modules can extend it
// (see other-module/UpiPending.kt).

sealed class PaymentResult(val orderId: String) {
    class Paid(orderId: String, val receiptUrl: String) : PaymentResult(orderId)
    class Declined(orderId: String, val reason: String) : PaymentResult(orderId)
    open class Pending(orderId: String) : PaymentResult(orderId)
}

fun describe(result: PaymentResult): String = when (result) {
    is PaymentResult.Paid -> "paid ${result.orderId}"
    is PaymentResult.Declined -> "declined ${result.orderId}: ${result.reason}"
    is PaymentResult.Pending -> "pending ${result.orderId}"   // also covers every subclass of Pending
    is Refunded -> "refunded ${result.orderId}"
}
