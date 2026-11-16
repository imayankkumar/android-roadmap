# Any, Unit and Nothing in Kotlin

> `Any` is the root of every non-null type: every value is an `Any`. `Unit` means "returns nothing useful" and has exactly one value; in bytecode it becomes `void`. `Nothing` has no values: a function that returns it never returns at all, so the compiler knows the code after it can't run.

**Read:** [What do Any, Unit and Nothing mean in Kotlin?](https://imayankkumar.com/blog/kotlin-any-unit-nothing/) · **Practice:** [Interview questions](https://imayankkumar.com/interview/kotlin-any-unit-nothing/)

Run [Main.kt](src/main/kotlin/com/imayankkumar/roadmap/anyunitnothing/Main.kt) to see what every step prints. Each step below shows the key Kotlin lines next to the Java the compiler makes from them. The tests in [AnyUnitNothingTest.kt](src/test/kotlin/com/imayankkumar/roadmap/anyunitnothing/AnyUnitNothingTest.kt) prove each claim.

## Step 1: `Any`

Post section: [What is Any in Kotlin?](https://imayankkumar.com/blog/kotlin-any-unit-nothing/#what-is-any-in-kotlin) · Code: [Step1Any.kt](src/main/kotlin/com/imayankkumar/roadmap/anyunitnothing/Step1Any.kt) · Java: [Step1AnyKt.java](decompiled/com/imayankkumar/roadmap/anyunitnothing/Step1AnyKt.java)

```kotlin
fun describe(value: Any?): String = when (value) {
    null -> "null"
    is Int -> "Int $value"
    is String -> "String \"$value\" (${value.length} chars)"
    else -> value.toString()
}
```

```java
public static final String describe(@Nullable Object value) {
    Object var1 = value;
    return var1 == null
        ? "null"
        : (var1 instanceof Integer
            ? "Int " + value
            : (var1 instanceof String ? "String \"" + value + "\" (" + ((String)value).length() + " chars)" : value.toString()));
}
```

**What to notice:** `Any?` becomes Java's `Object`. The `is` checks become `instanceof`, and the smart cast becomes a plain cast, `(String)value`.

## Step 2: `Unit`

Post section: [What is Unit, and how is it different from void?](https://imayankkumar.com/blog/kotlin-any-unit-nothing/#what-is-unit-and-how-is-it-different-from-void) · Code: [Step2Unit.kt](src/main/kotlin/com/imayankkumar/roadmap/anyunitnothing/Step2Unit.kt) · Java: [Step2UnitKt.java](decompiled/com/imayankkumar/roadmap/anyunitnothing/Step2UnitKt.java)

```kotlin
fun clearCart() {
    cartItems.clear()
}

fun clearCartTimed(): Unit = timed { clearCart() }   // Unit used as a value
```

```java
public static final void clearCart() {
    ModelKt.getCartItems().clear();
}

private static final Unit clearCartTimed$lambda$0() {
    clearCart();
    return Unit.INSTANCE;
}
```

**What to notice:** a function that returns `Unit` is a normal `void` method, so it costs nothing. When `Unit` is used as a value, for example as the result of a generic function, the compiler returns the single `Unit.INSTANCE` object.

## Step 3: `Nothing`

Post section: [What is Nothing, and why would a function return it?](https://imayankkumar.com/blog/kotlin-any-unit-nothing/#what-is-nothing-and-why-would-a-function-return-it) · Code: [Step3Nothing.kt](src/main/kotlin/com/imayankkumar/roadmap/anyunitnothing/Step3Nothing.kt) · Java: [Step3NothingKt.java](decompiled/com/imayankkumar/roadmap/anyunitnothing/Step3NothingKt.java)

```kotlin
fun stopOrder(reason: String): Nothing = throw IllegalStateException(reason)

fun orderTotal(orderId: String): Int {
    val order = findOrder(orderId) ?: stopOrder("Order $orderId not found")
    return order.total                 // order is Order here, no !! needed
}
```

```java
public static final Void stopOrder(@NotNull String reason) {
    Intrinsics.checkNotNullParameter(reason, "reason");
    throw new IllegalStateException(reason);
}

public static final int orderTotal(@NotNull String orderId) {
    Intrinsics.checkNotNullParameter(orderId, "orderId");
    Order var10000 = ModelKt.findOrder(orderId);
    if (var10000 == null) {
        stopOrder("Order " + orderId + " not found");
        throw new KotlinNothingValueException();
    } else {
        Order order = var10000;
        return order.getTotal();
    }
}
```

**What to notice:** `Nothing` becomes `java.lang.Void`. Right after the call, the compiler adds `throw new KotlinNothingValueException()`. It never runs, but it tells the JVM that this path really ends here. Also see `nullFitsAnywhere()` in the Java file: `val x = null` comes out as `Void x = null`, because its Kotlin type is `Nothing?`. (Decompiled code shows types like this even when it isn't valid Java you could compile.)

## Step 4: a loading state with `Nothing`

Post section: [Why can a loading state be UiState<Nothing>?](https://imayankkumar.com/blog/kotlin-any-unit-nothing/#why-can-a-loading-state-be-uistatenothing) · Code: [Step4LoadingState.kt](src/main/kotlin/com/imayankkumar/roadmap/anyunitnothing/Step4LoadingState.kt) · Java: [OrderState.java](decompiled/com/imayankkumar/roadmap/anyunitnothing/OrderState.java), [Step4LoadingStateKt.java](decompiled/com/imayankkumar/roadmap/anyunitnothing/Step4LoadingStateKt.java)

```kotlin
sealed interface OrderState<out T> {
    data object Loading : OrderState<Nothing>
    data class Ready<T>(val data: T) : OrderState<T>
    data class Failed(val message: String) : OrderState<Nothing>
}

val orderScreen: OrderState<Order> = OrderState.Loading
val cartScreen: OrderState<List<CartItem>> = OrderState.Loading
```

```java
private static final OrderState<Order> orderScreen = OrderState.Loading.INSTANCE;
private static final OrderState<List<CartItem>> cartScreen = OrderState.Loading.INSTANCE;
```

**What to notice:** `out T` and `Nothing` are checked only by the Kotlin compiler. In bytecode, generics are erased, and both screens share the same single `Loading.INSTANCE` object. Without `out`, the same line doesn't compile (see below).

## Won't compile

Paste these into the [playground](../../playground) (or into this topic's `src/main/kotlin`, where the file says so) to see the errors.

| File | Error |
| --- | --- |
| [Step1AnyHasNoWait.kt.txt](wont-compile/Step1AnyHasNoWait.kt.txt) | Unresolved reference 'wait' on receiver of type 'Any'. |
| [Step3UnitInsteadOfNothing.kt.txt](wont-compile/Step3UnitInsteadOfNothing.kt.txt) | Only safe (?.) or non-null asserted (!!.) calls are allowed on a nullable receiver of type 'Order?'. |
| [Step4WithoutOut.kt.txt](wont-compile/Step4WithoutOut.kt.txt) | Initializer type mismatch: expected 'PlainOrderState<Order>', actual 'PlainOrderState.Loading'. |

Errors checked with Kotlin 2.4.21.

## Try this

1. Change `stopOrder` to return `Unit` instead of `Nothing`. What breaks in `orderTotal`, and why?
2. Remove `out` from `OrderState<out T>`. Which lines stop compiling?
3. Write `fun todo(): Int = TODO()` and decompile. What does `TODO()` turn into?

## Further reading

- [Unit-returning functions](https://kotlinlang.org/docs/functions.html#unit-returning-functions) and [the Nothing type](https://kotlinlang.org/docs/exceptions.html#the-nothing-type) on kotlinlang.org
- [Generics: variance](https://kotlinlang.org/docs/generics.html#variance)
