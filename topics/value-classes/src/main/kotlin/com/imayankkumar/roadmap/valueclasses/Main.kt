package com.imayankkumar.roadmap.valueclasses

// Run main() from the IDE gutter to see every step's output in one place.

fun main() {
    println("Step 1: a value class")
    println("  ${Email("asha@example.com").value}")
    println("  ${runCatching { Email("asha.example.com") }.exceptionOrNull()?.message}")

    println("Step 2: typealias or normal class")
    println("  swap with a typealias compiles: ${swapWithAliasCompiles("asha@example.com", "+91 98765 43210")}")

    println("Step 3: what the compiler makes")
    print("  ")
    createAndSend()

    println("Step 4: when it creates an object")
    val email = Email("asha@example.com")
    println("  as Any: ${asAny(email).javaClass.simpleName}")
    val list: List<Any> = inList(email)
    println("  in a List: ${list[0].javaClass.simpleName}")
    println("  as text: ${asText(email)}")

    println("Step 5: calling from Java")
    println("  ${sendOtpFromJava(email, PhoneNumber("+91 98765 43210"))}")
}
