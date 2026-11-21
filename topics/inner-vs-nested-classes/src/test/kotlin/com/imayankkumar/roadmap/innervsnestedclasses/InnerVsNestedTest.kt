package com.imayankkumar.roadmap.innervsnestedclasses

import java.lang.reflect.Modifier
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

// One test per claim in https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/

class Step1NestedTest {

    @Test
    fun `a nested class needs no outer object`() {
        assertEquals("Restaurant", firstStop().name)
    }

    @Test
    fun `an inner class reads the outer members and this@Outer is the outer object`() {
        val tracker = OrderTracker("A12")
        val label = labelFor(tracker)
        assertEquals("Order A12", label.text())
        assertSame(tracker, label.tracker())
    }
}

class Step2GeneratedTest {

    private fun fieldNames(type: Class<*>) = type.declaredFields.map { it.name }

    @Test
    fun `a nested class is static and has no this$0`() {
        assertTrue(Modifier.isStatic(OrderTracker.DeliveryStop::class.java.modifiers))
        assertFalse("this$0" in fieldNames(OrderTracker.DeliveryStop::class.java))
    }

    @Test
    fun `an inner class is not static and has a synthetic package-private this$0`() {
        val inner = OrderTracker.StatusLabel::class.java
        assertFalse(Modifier.isStatic(inner.modifiers))
        val field = inner.getDeclaredField("this$0")
        assertTrue(field.isSynthetic)
        assertFalse(Modifier.isPublic(field.modifiers) || Modifier.isPrivate(field.modifiers))
    }

    @Test
    fun `the inner constructor takes the outer object first`() {
        val constructor = OrderTracker.StatusLabel::class.java.declaredConstructors.single()
        assertEquals(listOf(OrderTracker::class.java), constructor.parameterTypes.toList())
    }

    @Test
    fun `private members used from an inner class get access helper methods`() {
        val helpers = Bill::class.java.declaredMethods.filter { it.isSynthetic }.map { it.name }
        assertTrue("access\$getTotal\$p" in helpers)
        assertTrue("access\$setTotal\$p" in helpers)
    }

    @Test
    fun `a nested class can read private members through an instance`() {
        val bill = Bill(100)
        bill.AddItem().add(60)
        assertEquals(160, Bill.Peek().totalOf(bill))
    }

    @Test
    fun `a lambda keeps the outer object only if it uses it`() {
        // Lambdas compile to a private static method. Its parameters show what the lambda keeps.
        fun lambdaParameters(prefix: String) =
            Bill::class.java.declaredMethods.single { it.name.startsWith("$prefix\$lambda") }.parameterTypes.toList()

        assertEquals(emptyList(), lambdaParameters("lambdaWithoutBill"))
        assertEquals(listOf(Bill::class.java), lambdaParameters("lambdaWithBill"))
        assertEquals("total 5", Bill(5).lambdaWithBill()())
    }
}

class Step3LeakTest {

    @AfterTest
    fun clear() = OrderUpdates.clear()

    @Test
    fun `after a rotation the old screen is still held through this$0`() {
        val first = TrackingScreenAlmostRight().apply { onCreate(); onStart() }
        rotate(first) { TrackingScreenAlmostRight() }

        val held = OrderUpdates.listeners().map { outerOf(it) }
        assertEquals(2, held.size)
        assertSame(first, held.first())
        assertTrue(first.isDestroyed)
    }

    @Test
    fun `removing the listener in onStop leaves only the live screen`() {
        val first = TrackingScreenRight().apply { onCreate(); onStart() }
        val second = rotate(first) { TrackingScreenRight() }

        val held = OrderUpdates.listeners().map { outerOf(it) }
        assertEquals(listOf<Any?>(second), held)
        OrderUpdates.send("On the way")
        assertEquals("On the way", second.statusText)
        assertEquals("", first.statusText)
    }
}

class Step4WhenInnerTest {

    @Test
    fun `a holder calls its adapter`() {
        var clicked = ""
        MenuAdapter { clicked = it }.holderFor("Paneer roll").click()
        assertEquals("Paneer roll", clicked)
    }

    @Test
    fun `a nested class that gets one value holds no outer object`() {
        val header = OrderSummary("A12", listOf("Lassi")).header()
        assertEquals("Order A12", header.text())
        assertFalse(header.javaClass.declaredFields.any { it.type == OrderSummary::class.java })
    }
}
