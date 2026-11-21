package com.imayankkumar.roadmap.sealedvsenum

// Same module, same package, another file: allowed.
class Refunded(orderId: String) : PaymentResult(orderId)
