package com.imayankkumar.roadmap.sealedvsenum

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class IndirectSubclassTest {

    @Test
    fun `a subclass of an open subclass can live in another module`() {
        assertEquals("pending C3", describe(UpiPending("C3", "mayank@upi")))
    }

    @Test
    fun `an indirect subclass is not in the sealed list`() {
        assertFalse(UpiPending::class.java in PaymentResult::class.java.permittedSubclasses)
    }
}
