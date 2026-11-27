package com.imayankkumar.roadmap.objects

import java.lang.reflect.Modifier
import java.util.Collections
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

// One test per claim in https://imayankkumar.com/blog/kotlin-object/

class Step1ObjectDeclarationTest {

    @Test
    fun `every constructor call is a new instance`() {
        assertNotSame(SessionClass(), SessionClass())
        assertNull(classLosesTheEmail())
    }

    @Test
    fun `an object is the same instance everywhere`() {
        assertEquals("hello@imayankkumar.com", objectKeepsTheEmail())
        assertSame(Session, Session)
    }
}

class Step2WhatTheCompilerMakesTest {

    @Test
    fun `the one instance is a public static final INSTANCE field`() {
        val field = Session::class.java.getField("INSTANCE")
        assertTrue(Modifier.isStatic(field.modifiers))
        assertTrue(Modifier.isFinal(field.modifiers))
        assertSame(Session, field.get(null))
    }

    @Test
    fun `the constructor is private`() {
        val constructors = Session::class.java.declaredConstructors
        assertEquals(1, constructors.size)
        assertTrue(Modifier.isPrivate(constructors.single().modifiers))
    }
}

class Step3WhenItIsCreatedTest {

    @Test
    fun `properties and init run in the order you wrote them`() {
        AppConfig.second
        assertEquals(
            listOf("AppConfig: first property", "AppConfig: init block", "AppConfig: second property"),
            creationLog.filter { it.startsWith("AppConfig") },
        )
    }

    @Test
    fun `reading a const val does not create the object`() {
        assertEquals("2.4", BuildInfo.VERSION)
        assertFalse("BuildInfo: created" in creationLog)

        BuildInfo.createdAt
        assertTrue("BuildInfo: created" in creationLog)
    }

    @Test
    fun `many threads at once still get one instance`() {
        val start = CountDownLatch(1)
        val seen = Collections.synchronizedSet(mutableSetOf<Int>())
        val threads = List(8) {
            thread {
                start.await()
                seen += System.identityHashCode(Registry)
            }
        }
        start.countDown()
        threads.forEach { it.join() }

        assertEquals(1, seen.size)
        assertEquals(1, registryCreations.get())
    }

    @Test
    fun `an AtomicInteger inside an object keeps every update`() {
        val before = Hits.safeCount.get()
        val threads = List(8) { thread { repeat(10_000) { Hits.safeCount.incrementAndGet() } } }
        threads.forEach { it.join() }
        assertEquals(before + 80_000, Hits.safeCount.get())
    }
}

class Step4ObjectExpressionTest {

    @Test
    fun `an object expression is a new instance every time`() {
        assertNotSame(makeListener(), makeListener())
    }

    @Test
    fun `removing with a second object expression removes nothing`() {
        val button = LoginButton()
        addThenRemoveWithTwoObjects(button)
        assertEquals(1, button.listenerCount)
    }

    @Test
    fun `removing with the kept reference works`() {
        val button = LoginButton()
        val screen = LoginScreen(button)
        screen.onStart()
        assertEquals(1, button.listenerCount)
        screen.onStop()
        assertEquals(0, button.listenerCount)
    }

    @Test
    fun `an object expression can change a local var`() {
        val button = LoginButton()
        val count = countLogins(button)
        button.login("hello@imayankkumar.com")
        button.login("hello@imayankkumar.com")
        assertEquals(2, count())
    }
}

class Step5DataObjectTest {

    @Test
    fun `toString is only the name`() {
        assertEquals("LoggedOut", AuthState.LoggedOut.toString())
        assertEquals("Guest", Guest.toString())
        assertEquals("LoggedIn(email=hello@imayankkumar.com)", AuthState.LoggedIn("hello@imayankkumar.com").toString())
    }

    @Test
    fun `a plain object prints its class name and a hash`() {
        assertTrue(PlainAuthState.LoggedOut.toString().contains("PlainAuthState\$LoggedOut@"))
    }

    @Test
    fun `a second instance made by reflection is equal, but not the same`() {
        val constructor = AuthState.LoggedOut::class.java.getDeclaredConstructor().apply { isAccessible = true }
        val second = constructor.newInstance()

        assertEquals<Any>(AuthState.LoggedOut, second)
        assertEquals(AuthState.LoggedOut.hashCode(), second.hashCode())
        assertFalse(AuthState.LoggedOut === second)
    }

    @Test
    fun `a plain object made twice is not equal`() {
        val constructor = PlainAuthState.LoggedOut::class.java.getDeclaredConstructor().apply { isAccessible = true }
        assertNotEquals<Any>(PlainAuthState.LoggedOut, constructor.newInstance())
    }

    @Test
    fun `a data object has no copy or component1`() {
        val names = AuthState.LoggedOut::class.java.declaredMethods.map { it.name }
        assertFalse("copy" in names)
        assertFalse("component1" in names)
    }
}
