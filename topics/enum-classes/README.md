# Enum classes in Kotlin

> Use an enum class when a value must be one of a small, fixed set that you know at compile time, and every option has the same shape. Each constant is a single object. It can carry values and its own behavior. When the options need different data, use a sealed class instead.

**Read:** [When should you use an enum class in Kotlin?](https://imayankkumar.com/blog/kotlin-enum-class/) · **Practice:** [Interview questions](https://imayankkumar.com/interview/kotlin-enum-class/)

Run [Main.kt](src/main/kotlin/com/imayankkumar/roadmap/enumclasses/Main.kt) to see what every step prints. Each step below shows the key Kotlin lines next to the Java the compiler makes from them. The tests in [EnumClassTest.kt](src/test/kotlin/com/imayankkumar/roadmap/enumclasses/EnumClassTest.kt) prove each claim.

## Step 1: an enum class

Post section: [What is an enum class in Kotlin?](https://imayankkumar.com/blog/kotlin-enum-class/#what-is-an-enum-class-in-kotlin) · Code: [Step1EnumClass.kt](src/main/kotlin/com/imayankkumar/roadmap/enumclasses/Step1EnumClass.kt) · Java: [SimpleStatus.java](decompiled/com/imayankkumar/roadmap/enumclasses/SimpleStatus.java)

```kotlin
enum class SimpleStatus {
    PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED
}
```

```java
public enum SimpleStatus {
    PLACED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED;
}
```

**What to notice:** a Kotlin enum is a plain Java enum. Each constant is a `static final` object created once when the class loads, so `valueOf("PREPARING") === PREPARING` is `true`. `name` and `ordinal` come from `java.lang.Enum`.

## Step 2: values and functions

Post section: [How do you add values and functions to an enum?](https://imayankkumar.com/blog/kotlin-enum-class/#how-do-you-add-values-and-functions-to-an-enum) · Code: [Step2ValuesAndFunctions.kt](src/main/kotlin/com/imayankkumar/roadmap/enumclasses/Step2ValuesAndFunctions.kt) · Java: [OrderStatus.java](decompiled/com/imayankkumar/roadmap/enumclasses/OrderStatus.java), [DeliverySpeed.java](decompiled/com/imayankkumar/roadmap/enumclasses/DeliverySpeed.java)

```kotlin
enum class DeliverySpeed {
    STANDARD {
        override fun deliveryCharge(orderTotal: Int) = if (orderTotal >= 500) 0 else 30
    },
    EXPRESS {
        override fun deliveryCharge(orderTotal: Int) = 60
    };

    abstract fun deliveryCharge(orderTotal: Int): Int
}
```

```java
public enum DeliverySpeed {
    STANDARD,
    EXPRESS;

    public abstract int deliveryCharge(int var1);

    static final class STANDARD extends DeliverySpeed {
        public int deliveryCharge(int orderTotal) {
            return orderTotal >= 500 ? 0 : 30;
        }
    }
}
```

**What to notice:** a constant with its own body becomes its own class, `DeliverySpeed$STANDARD`, that extends the enum. So `STANDARD.javaClass` is not `DeliverySpeed`. Use `declaringJavaClass` if you need the enum type. `OrderStatus.java` shows the other shape: one `code` field filled by a constructor.

## Step 3: `when` without `else`

Post section: [Why doesn't when need an else branch?](https://imayankkumar.com/blog/kotlin-enum-class/#why-doesnt-when-need-an-else-branch) · Code: [Step3ExhaustiveWhen.kt](src/main/kotlin/com/imayankkumar/roadmap/enumclasses/Step3ExhaustiveWhen.kt) · Java: [Step3ExhaustiveWhenKt.java](decompiled/com/imayankkumar/roadmap/enumclasses/Step3ExhaustiveWhenKt.java)

```kotlin
fun label(status: OrderStatus): String = when (status) {
    OrderStatus.PLACED -> "Order placed"
    OrderStatus.PREPARING -> "Being prepared"
    OrderStatus.OUT_FOR_DELIVERY -> "On the way"
    OrderStatus.DELIVERED -> "Delivered"
}
```

```java
return switch (status) {
    case PLACED -> "Order placed";
    case PREPARING -> "Being prepared";
    case OUT_FOR_DELIVERY -> "On the way";
    case DELIVERED -> "Delivered";
    default -> throw new NoWhenBranchMatchedException();
};
```

**What to notice:** `when` on an enum becomes a real `switch`. In bytecode it goes through a small helper class, `Step3ExhaustiveWhenKt$WhenMappings`, that maps each constant's ordinal at runtime, so reordering the enum doesn't break compiled `when` code (the decompiler shows it as the `switch` above). Add `CANCELLED` and the build fails until you handle it (see [Step3MissingConstant.kt.txt](wont-compile/Step3MissingConstant.kt.txt)).

## Step 4: `entries`, not `values()`

Post section: [Should you use entries or values()?](https://imayankkumar.com/blog/kotlin-enum-class/#should-you-use-entries-or-values) · Code: [Step4Entries.kt](src/main/kotlin/com/imayankkumar/roadmap/enumclasses/Step4Entries.kt) · Java: [OrderStatus.java](decompiled/com/imayankkumar/roadmap/enumclasses/OrderStatus.java)

```kotlin
OrderStatus.values() !== OrderStatus.values()   // true: a new array each call
OrderStatus.entries === OrderStatus.entries     // true: one read-only List

inline fun <reified T : Enum<T>> names(): List<String> = enumEntries<T>().map { it.name }
```

```text
public static OrderStatus[] values();          // javap -c -p
   0: getstatic     // Field $VALUES
   3: invokevirtual // Method java/lang/Object.clone

public static EnumEntries<OrderStatus> getEntries();
   0: getstatic     // Field $ENTRIES
   3: areturn
```

**What to notice:** `values()` clones the hidden `$VALUES` array every time, because whoever gets an array could change it. `entries` returns the same `$ENTRIES` list every time. The decompiler hides `values()` because it is generated, so this part comes from `./gradlew :enum-classes:bytecode`.

## Step 5: saving an enum

Post section: [Why is saving ordinal or name risky?](https://imayankkumar.com/blog/kotlin-enum-class/#why-is-saving-ordinal-or-name-risky) · Code: [Step5SavingEnums.kt](src/main/kotlin/com/imayankkumar/roadmap/enumclasses/Step5SavingEnums.kt) · Java: [Step5SavingEnumsKt.java](decompiled/com/imayankkumar/roadmap/enumclasses/Step5SavingEnumsKt.java)

`Prefs` stands in for SharedPreferences, and `StatusV2` is the next release of the app: `CONFIRMED` is added after `PLACED`, and `PREPARING` is renamed to `COOKING`.

```kotlin
// Almost right: saved ordinal 1 (PREPARING) reads back as CONFIRMED
prefs.putInt("last_status", status.ordinal)
StatusV2.entries[prefs.getInt("last_status", 0)]

// Almost right: saved "PREPARING" crashes after the rename
StatusV2.valueOf(prefs.getString("last_status", null)!!)

// Right: save a code you control, and handle codes you don't know
prefs.putString("last_status", status.code)
StatusV2.fromCode(prefs.getString("last_status", null)) ?: StatusV2.PLACED
```

```java
prefs.putInt("last_status", status.ordinal());
return (StatusV2)StatusV2.getEntries().get(prefs.getInt("last_status", 0));
```

**What to notice:** the saved `1` is just a number. Nothing in it says "PREPARING", so after the update it means whatever is second in the list. The tests show all three outcomes: wrong data, a crash, and the code that still finds `COOKING`. A status the server adds later, like `"ready_for_pickup"`, gets a safe default instead of a crash.

## Step 6: enum or sealed class?

Post section: [Enum or sealed class?](https://imayankkumar.com/blog/kotlin-enum-class/#enum-or-sealed-class) · Code: [Step6EnumOrSealed.kt](src/main/kotlin/com/imayankkumar/roadmap/enumclasses/Step6EnumOrSealed.kt) · Java: [SortOrder.java](decompiled/com/imayankkumar/roadmap/enumclasses/SortOrder.java), [CheckoutState.java](decompiled/com/imayankkumar/roadmap/enumclasses/CheckoutState.java)

```kotlin
enum class SortOrder(val label: String) { PRICE("Price"), RATING("Rating") }

sealed interface CheckoutState {
    data object Loading : CheckoutState
    data class Ready(val total: Int) : CheckoutState
    data class Failed(val message: String) : CheckoutState
}
```

**What to notice:** `SortOrder.PRICE` is one object whose label is fixed when you write the code. `Failed("Timeout")` is a new object made at runtime with its own message, which an enum can't do. The [sealed class topic](../sealed-vs-enum) covers that side.

## Won't compile

Paste these into the [playground](../../playground) to see the errors.

| File | Error |
| --- | --- |
| [Step2EnumExtendsClass.kt.txt](wont-compile/Step2EnumExtendsClass.kt.txt) | Enum classes cannot extend classes. |
| [Step3MissingConstant.kt.txt](wont-compile/Step3MissingConstant.kt.txt) | 'when' expression must be exhaustive. Add the 'CANCELLED' branch or an 'else' branch. |

Errors checked with Kotlin 2.4.21.

## Try this

1. Move `DELIVERED` to the top of `OrderStatus`. Which tests fail, and does `label()` still work?
2. Add `READY_FOR_PICKUP("ready_for_pickup")` to `OrderStatus`. Which file stops compiling first?
3. Run `./gradlew :enum-classes:bytecode` and find `$VALUES` and `$ENTRIES` in `OrderStatus`. Where are they filled?

## Further reading

- [Enum classes](https://kotlinlang.org/docs/enum-classes.html) on kotlinlang.org
- [Stable replacement of values()](https://kotlinlang.org/docs/whatsnew19.html#stable-replacement-of-the-enum-class-values-function) in What's new in Kotlin 1.9.0
