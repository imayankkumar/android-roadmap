package com.imayankkumar.roadmap.nullsafety

// Post section: "Where can a NullPointerException still happen in Kotlin?"
// https://imayankkumar.com/blog/kotlin-null-safety/#where-can-a-nullpointerexception-still-happen-in-kotlin
//
// At the boundary with Java. A Java String without annotations is String! in Kotlin,
// and Kotlin lets you treat it as either String or String?.

/** Compiles, then crashes on the assignment line when Java returns null. */
fun couponCrashes(orderId: String): String {
    val code: String = CouponService().getCouponCode(orderId)
    return code
}

/** Declaring the type as String? makes the compiler ask you to handle null. */
fun couponOrNone(orderId: String): String {
    val code: String? = CouponService().getCouponCode(orderId)
    return code ?: "No coupon"
}

/** With annotations on the Java side, Kotlin already sees String?, so nobody can forget. */
fun couponFromAnnotated(orderId: String): String =
    AnnotatedCouponService().getCouponCode(orderId) ?: "No coupon"
