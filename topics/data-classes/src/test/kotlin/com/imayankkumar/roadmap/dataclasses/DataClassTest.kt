package com.imayankkumar.roadmap.dataclasses

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

// One test per claim in https://imayankkumar.com/blog/kotlin-data-class/

class Step1DataVsRegularTest {

    @Test
    fun `data class compares by value, regular class by identity`() {
        assertTrue(sameDataItems())
        assertFalse(samePlainItems())
    }

    @Test
    fun `a set keeps one of two equal data items`() {
        assertEquals(1, dataSetSize())
        assertEquals(2, plainSetSize())
    }
}

class Step2GeneratedTest {

    @Test
    fun `toString lists the constructor properties`() {
        assertEquals("CartItem(name=Paneer roll, price=180, quantity=1)", printedItem())
    }

    @Test
    fun `componentN makes destructuring work`() {
        assertEquals(120, lineTotal(CartItem("Lassi", 60, 2)))
    }

    @Test
    fun `copy changes only the named values and makes a new object`() {
        val item = CartItem("Lassi", 60, 2)
        val more = withOneMore(item)
        assertEquals(CartItem("Lassi", 60, 3), more)
        assertNotSame(item, more)
    }

    @Test
    fun `equal objects have equal hash codes`() {
        assertEquals(CartItem("Lassi", 60, 2).hashCode(), CartItem("Lassi", 60, 2).hashCode())
    }
}

class Step3BodyPropertiesTest {

    @Test
    fun `body properties are ignored by equals`() {
        assertTrue(seenAndUnseenAreEqual())
    }

    @Test
    fun `copy resets body properties`() {
        assertFalse(copyKeepsSeen())
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class Step4ShallowCopyTest {

    private val lassi = CartItem("Lassi", 60, 1)

    @Test
    fun `copy shares the same list`() {
        val state = CartStateAlmostRight(mutableListOf())
        assertSame(state.items, state.copy().items)
    }

    @Test
    fun `mutating the list then copying means StateFlow never emits the update`() = runTest {
        val viewModel = CartViewModelAlmostRight()
        val seenSizes = mutableListOf<Int>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { seenSizes += it.items.size }
        }

        viewModel.add(lassi)

        assertEquals(listOf(0), seenSizes) // only the starting state, the update was skipped
    }

    @Test
    fun `a new list on every change makes StateFlow emit`() = runTest {
        val viewModel = CartViewModel()
        val seenSizes = mutableListOf<Int>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { seenSizes += it.items.size }
        }

        viewModel.add(lassi)

        assertEquals(listOf(0, 1), seenSizes)
    }
}

class Step5CopyVisibilityTest {

    @Test
    fun `the factory checks the value`() {
        assertFailsWith<IllegalArgumentException> { Coupon.of(90) }
        assertFailsWith<IllegalArgumentException> { SafeCoupon.of(90) }
    }

    @Test
    fun `a public copy skips the factory check`() {
        assertEquals(90, bypassCheck().percent)
    }
}
