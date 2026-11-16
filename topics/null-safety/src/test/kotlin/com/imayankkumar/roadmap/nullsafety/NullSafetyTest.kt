package com.imayankkumar.roadmap.nullsafety

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

// One test per claim in https://imayankkumar.com/blog/kotlin-null-safety/

class Step1NullableTypesTest {

    @Test
    fun `a null check smart casts String? to String`() {
        assertEquals(10, deliveryNoteLength(findOrder("A1")!!)) // "Ring twice"
        assertEquals(0, deliveryNoteLength(findOrder("B2")!!))
    }
}

class Step2NullToolsTest {

    @Test
    fun `smart cast uses the value only after the check`() {
        assertEquals(450, orderTotalOrZero(findOrder("A1")))
        assertEquals(0, orderTotalOrZero(null))
    }

    @Test
    fun `safe call chain stops at the first null`() {
        assertEquals("98xxxxxx01", riderPhone(findOrder("A1")))
        assertNull(riderPhone(findOrder("B2"))) // rider is null
        assertNull(riderPhone(null))            // order is null
    }

    @Test
    fun `elvis gives a default`() {
        assertEquals("Ring twice", noteOrDefault(findOrder("A1")))
        assertEquals("No note", noteOrDefault(findOrder("B2")))
    }

    @Test
    fun `elvis with return leaves the function early`() {
        assertEquals("Order A1: ₹450", checkoutSummary("A1"))
        assertEquals("Order Z9 not found", checkoutSummary("Z9"))
    }

    @Test
    fun `let runs only for a non-null value`() {
        assertEquals("Ravi is on the way", riderLabel(findOrder("A1")))
        assertEquals("Finding a rider", riderLabel(findOrder("B2")))
    }
}

class Step3BangBangTest {

    @Test
    fun `!! works while the value is there`() {
        assertEquals("Order A1 · ₹450", orderTitleAlmostRight("A1"))
    }

    @Test
    fun `!! moves the failure to runtime`() {
        assertFailsWith<NullPointerException> { orderTitleAlmostRight("Z9") }
    }

    @Test
    fun `handling the null case never crashes`() {
        assertEquals("Order not found", orderTitleRight("Z9"))
    }

    @Test
    fun `requireNotNull fails with your message`() {
        val error = assertFailsWith<IllegalArgumentException> { orderForRefund("Z9") }
        assertEquals("Refund asked for unknown order Z9", error.message)
    }
}

class Step4JavaInteropTest {

    @Test
    fun `a null from unannotated Java crashes on the assignment, not later`() {
        val error = assertFailsWith<NullPointerException> { couponCrashes("B2") }
        // Kotlin added this check when it saw a platform value go into a non-null String.
        assertTrue(error.message!!.contains("getCouponCode(...) must not be null"), error.message)
    }

    @Test
    fun `declaring String? turns the same call into a handled case`() {
        assertEquals("SAVE10", couponOrNone("A1"))
        assertEquals("No coupon", couponOrNone("B2"))
    }

    @Test
    fun `annotated Java is seen as String? by Kotlin`() {
        assertEquals("No coupon", couponFromAnnotated("B2"))
    }
}
