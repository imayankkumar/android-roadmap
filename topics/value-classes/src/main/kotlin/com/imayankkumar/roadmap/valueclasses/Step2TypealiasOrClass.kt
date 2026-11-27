package com.imayankkumar.roadmap.valueclasses

// Post section: "Why not use a typealias or a normal class?"
// https://imayankkumar.com/blog/kotlin-value-class/#why-not-use-a-typealias-or-a-normal-class
//
// A typealias is only a second name. A normal class is a new type, and a new object each time.

typealias EmailAlias = String

fun sendOtpWithAlias(email: EmailAlias, phone: String): String = "Sending OTP to $email and $phone"

// Compiles: to the compiler, EmailAlias and String are the same type.
fun swapWithAliasCompiles(email: EmailAlias, phone: String): String = sendOtpWithAlias(phone, email)

class EmailObject(val value: String)
