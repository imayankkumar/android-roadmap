package com.imayankkumar.roadmap.valueclasses

// Post section: "What is a value class in Kotlin?"
// https://imayankkumar.com/blog/kotlin-value-class/#what-is-a-value-class-in-kotlin
//
// One value, its own type. Email and PhoneNumber both hold a String,
// but the compiler treats them as two different types.

@JvmInline
value class Email(val value: String) {
    init {
        require("@" in value) { "Not an email: $value" }
    }
}

@JvmInline
value class PhoneNumber(val value: String)
