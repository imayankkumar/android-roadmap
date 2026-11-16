package com.imayankkumar.roadmap.anyunitnothing

// Post section: "What is Any in Kotlin?"
// https://imayankkumar.com/blog/kotlin-any-unit-nothing/#what-is-any-in-kotlin
//
// Any is the root of every non-null Kotlin type. Any? also allows null.
// It has only three members: equals(), hashCode() and toString().

/** Accepts any value, null included, because the type is Any?. */
fun logEvent(name: String, vararg params: Pair<String, Any?>): String =
    params.joinToString(prefix = "$name: ") { (key, value) -> "$key=${describe(value)}" }

/** `is` checks the real type at runtime and smart casts the value. */
fun describe(value: Any?): String = when (value) {
    null -> "null"
    is Int -> "Int $value"
    is String -> "String \"$value\" (${value.length} chars)"
    else -> value.toString()
}
