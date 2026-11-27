package com.imayankkumar.roadmap.objects

// Post section: "What is a data object?"
// https://imayankkumar.com/blog/kotlin-object/#what-is-a-data-object
//
// A data object is an object with a readable toString(), an equals() that checks
// the type, and a fixed hashCode(). There is no copy() or component1().

sealed interface AuthState {
    data object LoggedOut : AuthState
    data class LoggedIn(val email: String) : AuthState
}

// The same state with a plain object, to compare what toString() prints.
sealed interface PlainAuthState {
    object LoggedOut : PlainAuthState
}

// toString() is only the name, even when the data object has properties.
data object Guest {
    val name = "Guest user"
}
