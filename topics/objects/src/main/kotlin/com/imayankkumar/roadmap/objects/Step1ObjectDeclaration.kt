package com.imayankkumar.roadmap.objects

// Post section: "What is an object in Kotlin?"
// https://imayankkumar.com/blog/kotlin-object/#what-is-an-object-in-kotlin
//
// A class gives you a new instance every time you call its constructor.
// An object declaration is a class and its only instance, made in one step.

class SessionClass {
    var email: String? = null
}

object Session {
    var email: String? = null

    init {
        println("Session created")
    }

    fun isLoggedIn(): Boolean = email != null
}

// Login screen and profile screen, each calling the constructor.
fun classLosesTheEmail(): String? {
    SessionClass().email = "hello@imayankkumar.com" // login screen
    return SessionClass().email                      // profile screen: a new, empty instance
}

// Same two screens, using the object by its name.
fun objectKeepsTheEmail(): String? {
    Session.email = "hello@imayankkumar.com" // login screen
    return Session.email                      // profile screen: the same instance
}
