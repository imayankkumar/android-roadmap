package com.imayankkumar.roadmap.valueclasses

// Post section: "Frequently asked questions" (Can Java code call a value class?)
// https://imayankkumar.com/blog/kotlin-value-class/#frequently-asked-questions
//
// sendOtp() becomes sendOtp-<hash>, which Java can't call.
// @JvmName gives the function a normal name that Java can call.

@JvmName("sendOtpJava")
fun sendOtpFromJava(email: Email, phone: PhoneNumber): String =
    "Sending OTP to ${email.value} and ${phone.value}"
