package com.imayankkumar.roadmap.valueclasses

// Post section: "What does the compiler turn it into?"
// https://imayankkumar.com/blog/kotlin-value-class/#what-does-the-compiler-turn-it-into
//
// Open decompiled/.../Step3WhatTheCompilerMakesKt.java next to this file.

fun sendOtp(email: Email, phone: PhoneNumber) {
    println("Sending OTP to ${email.value} and ${phone.value}")
}

// The same code as main() in the post.
fun createAndSend() {
    val email = Email("asha@example.com")
    val phone = PhoneNumber("+91 98765 43210")
    sendOtp(email, phone)
}
