package com.imayankkumar.roadmap.enumclasses

// Run main() from the IDE gutter to see every step's output in one place.

fun main() {
    println("Step 1: an enum class")
    println("  PREPARING: name = ${SimpleStatus.PREPARING.name}, ordinal = ${SimpleStatus.PREPARING.ordinal}")

    println("Step 2: values and functions")
    println("  code = ${OrderStatus.PREPARING.code}, standard charge on 650 = ${DeliverySpeed.STANDARD.deliveryCharge(650)}")

    println("Step 3: when without else")
    println("  ${OrderStatus.entries.joinToString { label(it) }}")

    println("Step 4: entries")
    println("  values() new each time? ${valuesIsNewEachTime()}, entries same? ${entriesIsSameEachTime()}")
    println("  generic: ${names<DeliverySpeed>()}")

    println("Step 5: saving an enum, then updating the app")
    println("  saved ordinal of PREPARING, read back ${saveOrdinalThenUpdate(Prefs(), OrderStatus.PREPARING)}")
    println("  saved name: ${runCatching { saveNameThenUpdate(Prefs(), OrderStatus.PREPARING) }.exceptionOrNull()?.message}")
    println("  saved code, read back ${saveCodeThenUpdate(Prefs(), OrderStatus.PREPARING)}")

    println("Step 6: enum or sealed")
    println("  ${SortOrder.PRICE.label}, ${describe(CheckoutState.Failed("Timeout"))}")
}
