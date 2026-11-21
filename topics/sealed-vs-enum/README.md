# Sealed classes in Kotlin

> A sealed class (or sealed interface) is a type with a closed list of subtypes. The compiler knows all of them, so a `when` over it can check that you handled every case. Its direct subtypes must live in the same module and the same package.

**Read:** [When should you use a sealed class in Kotlin?](https://imayankkumar.com/blog/kotlin-sealed-class/) · **Practice:** [Interview questions](https://imayankkumar.com/interview/kotlin-sealed-class/)

Run [Main.kt](src/main/kotlin/com/imayankkumar/roadmap/sealedvsenum/Main.kt) to see what every step prints. Each step below shows the key Kotlin lines next to the Java the compiler makes from them. The tests in [SealedClassTest.kt](src/test/kotlin/com/imayankkumar/roadmap/sealedvsenum/SealedClassTest.kt) prove each claim.

This topic has a second module, [other-module](other-module), because some rules only show up across a module boundary.

## Step 1: a sealed type

Post section: [What is a sealed class in Kotlin?](https://imayankkumar.com/blog/kotlin-sealed-class/#what-is-a-sealed-class-in-kotlin) · Code: [Step1SealedType.kt](src/main/kotlin/com/imayankkumar/roadmap/sealedvsenum/Step1SealedType.kt) · Java: [OrderUiState.java](decompiled/com/imayankkumar/roadmap/sealedvsenum/OrderUiState.java), [Discount.java](decompiled/com/imayankkumar/roadmap/sealedvsenum/Discount.java)

```kotlin
sealed interface OrderUiState {
    data object Loading : OrderUiState
    data class Ready(val items: List<CartItem>, val total: Int) : OrderUiState
    data class Failed(val message: String) : OrderUiState
}
```

```java
public sealed interface OrderUiState permits OrderUiState.Failed, OrderUiState.Loading, OrderUiState.Ready {
    ...
}

public abstract sealed class Discount permits Discount.Coupon, Discount.FirstOrder {
    private Discount(int percent) { ... }
}
```

**What to notice:** with a Java 17 target, Kotlin writes the list of subtypes into the class file, the same `sealed ... permits` that Java 17 uses. A sealed class is `abstract`. Its constructor is `protected` in Kotlin, but in bytecode it is `private`, plus a synthetic public one that only the subclasses call (see `Discount.java`). Compare with `OrderScreenFields`, three fields that happily allow "loading and failed at the same time".

## Step 2: `when` without `else`

Post section: [Why doesn't when need an else branch?](https://imayankkumar.com/blog/kotlin-sealed-class/#why-doesnt-when-need-an-else-branch) · Code: [Step2ExhaustiveWhen.kt](src/main/kotlin/com/imayankkumar/roadmap/sealedvsenum/Step2ExhaustiveWhen.kt) · Java: [Step2ExhaustiveWhenKt.java](decompiled/com/imayankkumar/roadmap/sealedvsenum/Step2ExhaustiveWhenKt.java)

```kotlin
fun title(state: OrderUiState): String = when (state) {
    OrderUiState.Loading -> "Loading"
    is OrderUiState.Ready -> "Your order (${state.items.size} items)"
    is OrderUiState.Failed -> "Something went wrong"
}
```

```java
if (Intrinsics.areEqual(var1, OrderUiState.Loading.INSTANCE)) {
    var10000 = "Loading";
} else if (var1 instanceof OrderUiState.Ready) {
    var10000 = "Your order (" + ((OrderUiState.Ready)state).getItems().size() + " items)";
} else {
    if (!(var1 instanceof OrderUiState.Failed)) {
        throw new NoWhenBranchMatchedException();
    }
    var10000 = "Something went wrong";
}
```

**What to notice:** `when` becomes a chain of `instanceof` checks, and the smart cast becomes a plain cast. The compiler still adds a last `throw new NoWhenBranchMatchedException()`. It can only run if a class file changes without recompiling this code, for example a new subtype in a library you updated.

## Step 3: why `else` is almost right

Post section: [Why is else almost right?](https://imayankkumar.com/blog/kotlin-sealed-class/#why-is-else-almost-right) · Code: [Step3ElseAlmostRight.kt](src/main/kotlin/com/imayankkumar/roadmap/sealedvsenum/Step3ElseAlmostRight.kt) · Java: [Step3ElseAlmostRightKt.java](decompiled/com/imayankkumar/roadmap/sealedvsenum/Step3ElseAlmostRightKt.java)

```kotlin
// Almost right: compiles today, hides tomorrow's state
fun titleWithElse(state: TrackedOrderState): String = when (state) {
    is TrackedOrderState.Ready -> "Your order"
    is TrackedOrderState.Failed -> "Something went wrong"
    else -> "Loading"
}
```

```java
return var1 instanceof TrackedOrderState.Ready
    ? "Your order"
    : (var1 instanceof TrackedOrderState.Failed ? "Something went wrong" : "Loading");
```

**What to notice:** with `else`, everything that isn't `Ready` or `Failed` falls into "Loading", including `Cancelled`. The test `else turns a cancelled order into Loading` proves it. `titleByName` lists every state, so removing its `Cancelled` line fails the build (see [Step2MissingBranch.kt.txt](wont-compile/Step2MissingBranch.kt.txt)).

## Step 4: where subclasses can live

Post sections: [Where can the subclasses of a sealed class live?](https://imayankkumar.com/blog/kotlin-sealed-class/#where-can-the-subclasses-of-a-sealed-class-live) and [What about indirect subclasses?](https://imayankkumar.com/blog/kotlin-sealed-class/#what-about-indirect-subclasses) · Code: [Step4WhereSubclassesLive.kt](src/main/kotlin/com/imayankkumar/roadmap/sealedvsenum/Step4WhereSubclassesLive.kt), [Step4OtherFile.kt](src/main/kotlin/com/imayankkumar/roadmap/sealedvsenum/Step4OtherFile.kt), [other-module/UpiPending.kt](other-module/src/main/kotlin/com/imayankkumar/roadmap/sealedvsenum/UpiPending.kt) · Java: [PaymentResult.java](decompiled/com/imayankkumar/roadmap/sealedvsenum/PaymentResult.java), [UpiPending.java](other-module/decompiled/com/imayankkumar/roadmap/sealedvsenum/UpiPending.java)

```kotlin
sealed class PaymentResult(val orderId: String) {
    class Paid(orderId: String, val receiptUrl: String) : PaymentResult(orderId)
    class Declined(orderId: String, val reason: String) : PaymentResult(orderId)
    open class Pending(orderId: String) : PaymentResult(orderId)
}

class Refunded(orderId: String) : PaymentResult(orderId)               // another file: allowed

// other-module: allowed, because Pending is open
class UpiPending(orderId: String, val upiId: String) : PaymentResult.Pending(orderId)
```

```java
public abstract sealed class PaymentResult permits PaymentResult.Declined, PaymentResult.Paid, PaymentResult.Pending, Refunded {
    public static non-sealed class Pending extends PaymentResult { ... }
}
```

**What to notice:** `Refunded` is in another file and still in the `permits` list. `Pending` is marked `non-sealed`: it is in the list, but anyone can extend it, which is how `UpiPending` in the other module compiles. `UpiPending` is not in the list, and `describe()` handles it through its `is PaymentResult.Pending` branch. The [won't-compile](#wont-compile) files show the three places a direct subclass can't go: another package, another module, and a local or anonymous class.

## Step 5: sealed interface

Post section: [Sealed class or sealed interface?](https://imayankkumar.com/blog/kotlin-sealed-class/#sealed-class-or-sealed-interface) · Code: [Step5SealedInterface.kt](src/main/kotlin/com/imayankkumar/roadmap/sealedvsenum/Step5SealedInterface.kt) · Java: [ItemAdded.java](decompiled/com/imayankkumar/roadmap/sealedvsenum/ItemAdded.java), [SortOrder.java](decompiled/com/imayankkumar/roadmap/sealedvsenum/SortOrder.java), [Step5SealedInterfaceKt.java](decompiled/com/imayankkumar/roadmap/sealedvsenum/Step5SealedInterfaceKt.java)

```kotlin
sealed interface CartEvent
sealed interface AnalyticsEvent

data class ItemAdded(val name: String) : CartEvent, AnalyticsEvent
enum class SortOrder : CartEvent { PRICE, RATING }
```

```java
public final class ItemAdded implements CartEvent, AnalyticsEvent { ... }
public enum SortOrder implements CartEvent { ... }

} else if (var1 == SortOrder.PRICE) {
    var10000 = "sort by price";
}
```

**What to notice:** `ItemAdded` is in two closed lists, so both `handle()` and `track()` are exhaustive without `else`. Enum constants can be part of a sealed `when` too, and each one is checked with `==`, because every constant is a single object.

## Won't compile

Paste these into the [playground](../../playground) (or into the module the file names) to see the errors.

| File | Error |
| --- | --- |
| [Step1PublicConstructor.kt.txt](wont-compile/Step1PublicConstructor.kt.txt) | Constructor must be private or protected in sealed class. |
| [Step2MissingBranch.kt.txt](wont-compile/Step2MissingBranch.kt.txt) | 'when' expression must be exhaustive. Add the 'is Cancelled' branch or an 'else' branch. |
| [Step4OtherPackage.kt.txt](wont-compile/Step4OtherPackage.kt.txt) | A class can only extend a sealed class or interface declared in the same package. |
| [Step4OtherModule.kt.txt](wont-compile/Step4OtherModule.kt.txt) | Extending sealed classes or interfaces from a different module is prohibited. |
| [Step4LocalClass.kt.txt](wont-compile/Step4LocalClass.kt.txt) | Local class cannot extend a sealed interface. / Anonymous object cannot extend a sealed interface. |

Errors checked with Kotlin 2.4.21.

## Try this

1. Add `data class Cancelled(val reason: String)` to `OrderUiState`. Which functions stop compiling?
2. Make `Pending` sealed instead of open. What happens to `UpiPending` in the other module, and to `non-sealed` in the Java?
3. Change `jvmTarget` to `JVM_1_8` in `build-logic` and decompile. Where did `permits` go?

## Further reading

- [Sealed classes and interfaces](https://kotlinlang.org/docs/sealed-classes.html) on kotlinlang.org
- [JEP 409: Sealed Classes](https://openjdk.org/jeps/409), the Java 17 feature behind `permits`
