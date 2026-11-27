package com.imayankkumar.roadmap.valueclasses

import java.lang.reflect.Method
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

// One test per claim in https://imayankkumar.com/blog/kotlin-value-class/
//
// You can't see "no object" at runtime, so some tests read the compiled method
// signatures instead: a String parameter means no object, an Email or Age parameter means one.

private const val PACKAGE = "com.imayankkumar.roadmap.valueclasses"

private fun methodsOf(fileClass: String): List<Method> =
    Class.forName("$PACKAGE.$fileClass").declaredMethods.toList()

private fun List<Method>.named(prefix: String): Method =
    single { it.name == prefix || it.name.startsWith("$prefix-") }

class Step1ValueClassTest {

    @Test
    fun `init checks the value when you create it`() {
        assertEquals("asha@example.com", Email("asha@example.com").value)
        val error = assertFailsWith<IllegalArgumentException> { Email("asha.example.com") }
        assertEquals("Not an email: asha.example.com", error.message)
    }

    @Test
    fun `two value classes with the same value are equal`() {
        assertEquals(Email("asha@example.com"), Email("asha@example.com"))
    }
}

class Step2TypealiasOrClassTest {

    @Test
    fun `a typealias lets the swap through`() {
        assertEquals(
            "Sending OTP to +91 98765 43210 and asha@example.com",
            swapWithAliasCompiles(email = "asha@example.com", phone = "+91 98765 43210"),
        )
    }

    @Test
    fun `a normal class is a new object every time`() {
        assertNotSame(EmailObject("asha@example.com"), EmailObject("asha@example.com"))
    }
}

class Step3WhatTheCompilerMakesTest {

    private val sendOtp = methodsOf("Step3WhatTheCompilerMakesKt").named("sendOtp")

    @Test
    fun `the parameters are plain strings`() {
        assertEquals(listOf(String::class.java, String::class.java), sendOtp.parameterTypes.toList())
    }

    @Test
    fun `the function name gets a hash`() {
        assertTrue(sendOtp.name.startsWith("sendOtp-"), "was ${sendOtp.name}")
    }
}

class Step4WhenItCreatesAnObjectTest {

    private val methods = methodsOf("Step4WhenItCreatesAnObjectKt")
    private val email = Email("asha@example.com")

    @Test
    fun `no object as a parameter or return value`() {
        val method = methods.named("asParameter")
        assertEquals(listOf(String::class.java), method.parameterTypes.toList())
        assertEquals(String::class.java, method.returnType)
    }

    @Test
    fun `no object as a property of another class`() {
        assertEquals(String::class.java, User::class.java.getDeclaredField("email").type)
    }

    @Test
    fun `== and string templates work on the value`() {
        assertTrue(isSame(email, Email("asha@example.com")))
        assertEquals("Email(value=asha@example.com)", asText(email))
    }

    @Test
    fun `no object for a nullable Email`() {
        assertEquals(listOf(String::class.java), methods.named("nullableEmail").parameterTypes.toList())
    }

    @Test
    fun `an object as Any or a generic T`() {
        assertEquals<Class<*>?>(Email::class.java, asAny(email).javaClass)
        assertEquals<Class<*>?>(Email::class.java, genericEmail(email)?.javaClass)
    }

    @Test
    fun `an object in a List`() {
        val raw: List<Any> = inList(email)
        assertEquals<Class<*>?>(Email::class.java, raw[0].javaClass)
    }

    @Test
    fun `an object in a lambda that isn't inline`() {
        var seen: Any? = null
        val spy: (Any?) -> Unit = { seen = it }
        @Suppress("UNCHECKED_CAST")
        inLambda(email, spy as (Email) -> Unit)
        assertEquals<Class<*>?>(Email::class.java, seen?.javaClass)
    }

    @Test
    fun `an object for a nullable value class that wraps an Int`() {
        assertEquals(listOf(Age::class.java), methods.named("nullableAge").parameterTypes.toList())
        assertEquals(30, nullableAge(Age(30)))
    }
}

class Step5CallingFromJavaTest {

    @Test
    fun `@JvmName keeps a name Java can call`() {
        val method = assertNotNull(methodsOf("Step5CallingFromJavaKt").singleOrNull { it.name == "sendOtpJava" })
        assertEquals(listOf(String::class.java, String::class.java), method.parameterTypes.toList())
    }
}
