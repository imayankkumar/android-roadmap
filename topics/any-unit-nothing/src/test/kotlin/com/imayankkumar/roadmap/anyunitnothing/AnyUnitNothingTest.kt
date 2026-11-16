package com.imayankkumar.roadmap.anyunitnothing

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

// One test per claim in https://imayankkumar.com/blog/kotlin-any-unit-nothing/

class Step1AnyTest {

    @Test
    fun `a nullable Any takes values of every type, null included`() {
        assertEquals(
            "checkout: items=Int 3, coupon=String \"SAVE10\" (6 chars), note=null",
            logEvent("checkout", "items" to 3, "coupon" to "SAVE10", "note" to null),
        )
    }

    @Test
    fun `any other type falls back to toString, one of the three members of Any`() {
        assertEquals("Order(id=A1, total=240)", describe(Order("A1", 240)))
    }
}

class Step2UnitTest {

    @BeforeTest
    fun fillCart() {
        cartItems.clear()
        cartItems += CartItem("Paneer roll", 180)
    }

    @Test
    fun `a function with no return type returns Unit`() {
        val result = clearCart()
        assertEquals(Unit, result)
        assertEquals("kotlin.Unit", result.toString())
        assertTrue(cartItems.isEmpty())
    }

    @Test
    fun `there is only one Unit value`() {
        assertSame(Unit, clearCartTimed())
    }
}

class Step3NothingTest {

    @Test
    fun `a Nothing function lets the code after it use a non-null value`() {
        assertEquals(240, orderTotal("A1"))
    }

    @Test
    fun `a Nothing function never returns, it throws`() {
        val error = assertFailsWith<IllegalStateException> { orderTotal("Z9") }
        assertEquals("Order Z9 not found", error.message)
    }

    @Test
    fun `error() returns Nothing too`() {
        assertEquals(240, orderTotalWithError("A1"))
        assertFailsWith<IllegalStateException> { orderTotalWithError("Z9") }
    }

    @Test
    fun `val x = null fits any nullable type`() {
        val (name, order) = nullFitsAnywhere()
        assertNull(name)
        assertNull(order)
    }
}

class Step4LoadingStateTest {

    @Test
    fun `one Loading object fits screens with different data types`() {
        assertSame(OrderState.Loading, orderScreen)
        assertSame(OrderState.Loading, cartScreen)
    }

    @Test
    fun `every state gets a title`() {
        assertEquals("Loading your order", title(OrderState.Loading))
        assertEquals("Order A1: ₹240", title(OrderState.Ready(Order("A1", 240))))
        assertEquals("Something went wrong: No network", title(OrderState.Failed("No network")))
    }
}
