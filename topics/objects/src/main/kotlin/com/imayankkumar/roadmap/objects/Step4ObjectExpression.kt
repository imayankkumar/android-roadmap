package com.imayankkumar.roadmap.objects

// Post section: "What is an object expression?"
// https://imayankkumar.com/blog/kotlin-object/#what-is-an-object-expression
//
// object : LoginListener { ... } makes a new instance of an unnamed class
// every time the code runs. It is not a singleton.

interface LoginListener {
    fun onSuccess(email: String)
    fun onError(message: String)
}

class LoginButton {
    private val listeners = mutableListOf<LoginListener>()

    val listenerCount: Int get() = listeners.size

    fun addLoginListener(listener: LoginListener) {
        listeners += listener
    }

    fun removeLoginListener(listener: LoginListener) {
        listeners -= listener
    }

    fun login(email: String) {
        listeners.forEach { it.onSuccess(email) }
    }
}

fun showError(message: String) = println("Error: $message")

fun makeListener(): LoginListener = object : LoginListener {
    override fun onSuccess(email: String) { Session.email = email }
    override fun onError(message: String) { showError(message) }
}

// Almost right: the second object : is a new, different object, so nothing is removed.
fun addThenRemoveWithTwoObjects(button: LoginButton) {
    button.addLoginListener(object : LoginListener {
        override fun onSuccess(email: String) { Session.email = email }
        override fun onError(message: String) { showError(message) }
    })
    button.removeLoginListener(object : LoginListener {
        override fun onSuccess(email: String) { Session.email = email }
        override fun onError(message: String) { showError(message) }
    })
}

// Right: keep one reference and use it twice.
class LoginScreen(private val button: LoginButton) {
    private val listener = object : LoginListener {
        override fun onSuccess(email: String) { Session.email = email }
        override fun onError(message: String) { showError(message) }
    }

    fun onStart() = button.addLoginListener(listener)

    fun onStop() = button.removeLoginListener(listener)
}

// An object expression can change a local var around it.
// In the Java, count becomes an IntRef that the object holds.
fun countLogins(button: LoginButton): () -> Int {
    var count = 0
    button.addLoginListener(object : LoginListener {
        override fun onSuccess(email: String) { count++ }
        override fun onError(message: String) {}
    })
    return { count }
}
