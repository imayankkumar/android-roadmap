package com.imayankkumar.roadmap.enumclasses

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

// One test per claim in https://imayankkumar.com/blog/kotlin-enum-class/

class Step1EnumClassTest {

    @Test
    fun `name and ordinal come for free`() {
        assertEquals("PREPARING", SimpleStatus.PREPARING.name)
        assertEquals(1, SimpleStatus.PREPARING.ordinal)
    }

    @Test
    fun `each constant is a single object`() {
        assertTrue(sameObject())
    }
}

class Step2ValuesAndFunctionsTest {

    @Test
    fun `a constant carries its values`() {
        assertEquals("out_for_delivery", OrderStatus.OUT_FOR_DELIVERY.code)
        assertTrue(OrderStatus.PREPARING.isActive)
        assertFalse(OrderStatus.DELIVERED.isActive)
    }

    @Test
    fun `each constant can have its own behavior`() {
        assertEquals(0, DeliverySpeed.STANDARD.deliveryCharge(orderTotal = 650))
        assertEquals(30, DeliverySpeed.STANDARD.deliveryCharge(orderTotal = 300))
        assertEquals(60, DeliverySpeed.EXPRESS.deliveryCharge(orderTotal = 650))
    }

    @Test
    fun `a constant with a body is its own class`() {
        assertEquals(DeliverySpeed::class.java, DeliverySpeed.STANDARD.declaringJavaClass)
        assertTrue(DeliverySpeed.STANDARD.javaClass != DeliverySpeed::class.java)
    }
}

class Step3ExhaustiveWhenTest {

    @Test
    fun `when covers every constant`() {
        assertEquals(
            listOf("Order placed", "Being prepared", "On the way", "Delivered"),
            OrderStatus.entries.map { label(it) },
        )
    }
}

class Step4EntriesTest {

    @Test
    fun `values makes a new array every call, entries does not`() {
        assertTrue(valuesIsNewEachTime())
        assertTrue(entriesIsSameEachTime())
    }

    @Test
    fun `entries keeps the declaration order`() {
        assertEquals(listOf("PLACED", "PREPARING", "OUT_FOR_DELIVERY", "DELIVERED"), OrderStatus.entries.map { it.name })
    }

    @Test
    fun `enumEntries works in generic code`() {
        assertEquals(listOf("STANDARD", "EXPRESS"), names<DeliverySpeed>())
    }

    @Test
    fun `valueOf is case sensitive and throws on unknown names`() {
        assertFailsWith<IllegalArgumentException> { OrderStatus.valueOf("preparing") }
    }
}

class Step5SavingEnumsTest {

    @Test
    fun `a saved ordinal reads back the wrong status after a constant is added`() {
        assertEquals(StatusV2.CONFIRMED, saveOrdinalThenUpdate(Prefs(), OrderStatus.PREPARING))
    }

    @Test
    fun `a saved name crashes after the constant is renamed`() {
        assertFailsWith<IllegalArgumentException> { saveNameThenUpdate(Prefs(), OrderStatus.PREPARING) }
    }

    @Test
    fun `a saved code survives both changes`() {
        assertEquals(StatusV2.COOKING, saveCodeThenUpdate(Prefs(), OrderStatus.PREPARING))
    }

    @Test
    fun `an unknown code from the server gets a safe default instead of a crash`() {
        assertNull(OrderStatus.fromCode("ready_for_pickup"))
        assertEquals(OrderStatus.PLACED, readServerStatus("ready_for_pickup"))
        assertFailsWith<IllegalArgumentException> { OrderStatus.valueOf("READY_FOR_PICKUP") }
    }
}

class Step6EnumOrSealedTest {

    @Test
    fun `a sealed state carries data made at runtime`() {
        assertEquals("Error: Timeout", describe(CheckoutState.Failed("Timeout")))
        assertEquals("Price", SortOrder.PRICE.label)
    }
}
