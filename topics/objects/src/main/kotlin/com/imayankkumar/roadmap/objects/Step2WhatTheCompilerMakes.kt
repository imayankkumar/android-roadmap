package com.imayankkumar.roadmap.objects

// Post section: "What does the compiler turn it into?"
// https://imayankkumar.com/blog/kotlin-object/#what-does-the-compiler-turn-it-into
//
// Session (Step 1) becomes a final class with a static INSTANCE field and a private
// constructor. See decompiled/com/imayankkumar/roadmap/objects/Session.java.
//
// This file shows the call site: in the Java, Session.isLoggedIn() becomes
// Session.INSTANCE.isLoggedIn().

fun checkLogin(): Boolean = Session.isLoggedIn()
