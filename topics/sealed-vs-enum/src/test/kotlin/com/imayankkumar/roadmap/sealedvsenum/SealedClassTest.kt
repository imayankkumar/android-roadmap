package com.imayankkumar.roadmap.sealedvsenum

import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// One test per claim in https://imayankkumar.com/blog/kotlin-sealed-class/

class Step1SealedTypeTest {

    @Test
    fun `separate fields let a screen be loading and failed at once`() {
        assertTrue(impossibleScreen.isLoading && impossibleScreen.error != null)
    }

    @Test
    fun `the compiler writes down every subtype`() {
        // With a Java 17 target, Kotlin stores the list in the class file (PermittedSubclasses),
        // so even Java reflection can read it.
        assertTrue(OrderUiState::class.java.isSealed)
        assertEquals(
            setOf("Loading", "Ready", "Failed"),
            OrderUiState::class.java.permittedSubclasses.map { it.simpleName }.toSet(),
        )
    }

    @Test
    fun `a sealed class is abstract`() {
        assertTrue(Modifier.isAbstract(Discount::class.java.modifiers))
    }
}

class Step2ExhaustiveWhenTest {

    @Test
    fun `when covers every state and smart casts`() {
        assertEquals("Loading", title(OrderUiState.Loading))
        assertEquals("Your order (1 items)", title(OrderUiState.Ready(listOf(CartItem("Lassi", 60)), 60)))
        assertEquals("Something went wrong", title(OrderUiState.Failed("Timeout")))
    }
}

class Step3ElseAlmostRightTest {

    @Test
    fun `else turns a cancelled order into Loading`() {
        assertEquals("Loading", titleWithElse(TrackedOrderState.Cancelled("Restaurant closed")))
    }

    @Test
    fun `naming every state handles Cancelled`() {
        assertEquals("Cancelled: Restaurant closed", titleByName(TrackedOrderState.Cancelled("Restaurant closed")))
    }
}

class Step4WhereSubclassesLiveTest {

    @Test
    fun `a subclass in another file of the same package is a direct subtype`() {
        assertTrue(Refunded::class.java in PaymentResult::class.java.permittedSubclasses)
    }

    @Test
    fun `every subtype gets orderId from the parent`() {
        assertEquals("declined A1: Card expired", describe(PaymentResult.Declined("A1", "Card expired")))
        assertEquals("refunded B2", describe(Refunded("B2")))
    }
}

class Step5SealedInterfaceTest {

    @Test
    fun `one type can be in two sealed lists`() {
        val event = ItemAdded("Lassi")
        assertEquals("add Lassi", handle(event))
        assertEquals("item_added", track(event))
    }

    @Test
    fun `an enum can join a sealed interface`() {
        assertEquals("sort by rating", handle(SortOrder.RATING))
    }
}
