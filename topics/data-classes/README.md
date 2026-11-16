# Kotlin data class vs regular class

> A data class compares by value. For every property in its primary constructor, the compiler writes `equals()`, `hashCode()`, `toString()`, `componentN()` and `copy()`. A regular class compares by identity: two objects are equal only if they are the same object. `copy()` is shallow, so a mutable list inside a data class is shared between the old and new copy.

**Read:** [Kotlin data class vs regular class](https://imayankkumar.com/blog/kotlin-data-class/) · **Practice:** [Interview questions](https://imayankkumar.com/interview/kotlin-data-class/)

Run [Main.kt](src/main/kotlin/com/imayankkumar/roadmap/dataclasses/Main.kt) to see what every step prints. Each step below shows the key Kotlin lines next to the Java the compiler makes from them. The tests in [DataClassTest.kt](src/test/kotlin/com/imayankkumar/roadmap/dataclasses/DataClassTest.kt) prove each claim.

## Step 1: data class vs regular class

Post section: [What is a data class in Kotlin?](https://imayankkumar.com/blog/kotlin-data-class/#what-is-a-data-class-in-kotlin) · Code: [Step1DataVsRegular.kt](src/main/kotlin/com/imayankkumar/roadmap/dataclasses/Step1DataVsRegular.kt) · Java: [CartItem.java](decompiled/com/imayankkumar/roadmap/dataclasses/CartItem.java), [PlainCartItem.java](decompiled/com/imayankkumar/roadmap/dataclasses/PlainCartItem.java)

```kotlin
data class CartItem(val name: String, val price: Int, val quantity: Int)
class PlainCartItem(val name: String, val price: Int, val quantity: Int)

CartItem("Paneer roll", 180, 1) == CartItem("Paneer roll", 180, 1)            // true
PlainCartItem("Paneer roll", 180, 1) == PlainCartItem("Paneer roll", 180, 1)  // false
```

```java
// CartItem.java
public boolean equals(@Nullable Object other) {
    if (this == other) {
        return true;
    } else if (!(other instanceof CartItem var2)) {
        return false;
    } else if (!Intrinsics.areEqual(this.name, var2.name)) {
        return false;
    } else {
        return this.price != var2.price ? false : this.quantity == var2.quantity;
    }
}
```

**What to notice:** `PlainCartItem.java` has no `equals()` at all, so it uses `Object.equals()`, which only checks `this == other`. That is also why a `Set` keeps both plain items but only one data item.

## Step 2: what the compiler generates

Post section: [What does the compiler generate for a data class?](https://imayankkumar.com/blog/kotlin-data-class/#what-does-the-compiler-generate-for-a-data-class) · Code: [Step2Generated.kt](src/main/kotlin/com/imayankkumar/roadmap/dataclasses/Step2Generated.kt) · Java: [CartItem.java](decompiled/com/imayankkumar/roadmap/dataclasses/CartItem.java), [Step2GeneratedKt.java](decompiled/com/imayankkumar/roadmap/dataclasses/Step2GeneratedKt.java)

```kotlin
val (_, price, quantity) = item
item.copy(quantity = item.quantity + 1)
```

```java
// CartItem.java
public final int component2() { return this.price; }
public final int component3() { return this.quantity; }

public int hashCode() {
    int result = this.name.hashCode();
    result = result * 31 + Integer.hashCode(this.price);
    return result * 31 + Integer.hashCode(this.quantity);
}

// Step2GeneratedKt.java
int price = item.component2();
int quantity = item.component3();
return CartItem.copy$default(item, null, 0, item.getQuantity() + 1, 3, null);
```

**What to notice:** destructuring is just calls to `componentN()`, and `_` skips `component1()`. `copy()` with named arguments calls a hidden `copy$default` method. The `3` is a bit mask (`0b011`): bits 0 and 1 say "keep the old `name` and `price`", and only `quantity` is passed in. `hashCode()` mixes every constructor property with `* 31`, so equal objects get equal hash codes. The class is `public final`: a data class can't be open.

## Step 3: which properties count

Post section: [Which properties count in equals()?](https://imayankkumar.com/blog/kotlin-data-class/#which-properties-count-in-equals) · Code: [Step3BodyProperties.kt](src/main/kotlin/com/imayankkumar/roadmap/dataclasses/Step3BodyProperties.kt) · Java: [Order.java](decompiled/com/imayankkumar/roadmap/dataclasses/Order.java)

```kotlin
data class Order(val id: String, val items: List<CartItem>) {
    var isSeen: Boolean = false
}
```

```java
public final Order copy(@NotNull String id, @NotNull List<CartItem> items) { ... }

public int hashCode() {
    int result = this.id.hashCode();
    return result * 31 + this.items.hashCode();
}
```

**What to notice:** `isSeen` is a normal field with a getter and setter, but it is missing from `copy()`, `hashCode()`, `equals()` and `toString()`. So a seen and an unseen order are equal, and `copy()` resets `isSeen` to `false`.

## Step 4: copy() is shallow

Post section: [Is copy() a deep copy?](https://imayankkumar.com/blog/kotlin-data-class/#is-copy-a-deep-copy) · Code: [Step4ShallowCopy.kt](src/main/kotlin/com/imayankkumar/roadmap/dataclasses/Step4ShallowCopy.kt) · Java: [CartViewModelAlmostRight.java](decompiled/com/imayankkumar/roadmap/dataclasses/CartViewModelAlmostRight.java), [CartViewModel.java](decompiled/com/imayankkumar/roadmap/dataclasses/CartViewModel.java)

```kotlin
// Almost right
val current = _state.value
current.items.add(item)          // changes the list the OLD state also points to
_state.value = current.copy()    // equal to the old state, so StateFlow skips it

// Right
_state.update { it.copy(items = it.items + item) }
```

```java
// CartViewModelAlmostRight.java
current.getItems().add(item);
this._state.setValue(CartStateAlmostRight.copy$default(current, null, 1, null));

// CartViewModel.java
nextValue$iv = ((CartState)nextValue$iv).copy(CollectionsKt.plus(((CartState)nextValue$iv).getItems(), item));
```

**What to notice:** `copy$default` with mask `1` passes the same list reference into the new object. Nothing is cloned. The fixed version builds a new list with `plus`, so the new state is not equal to the old one and StateFlow emits it. The test `Step4ShallowCopyTest` collects the state and shows `[0]` for the almost-right version and `[0, 1]` for the fixed one.

## Step 5: copy() and a private constructor

Post section: [Why does copy() warn about a private constructor?](https://imayankkumar.com/blog/kotlin-data-class/#why-does-copy-warn-about-a-private-constructor) · Code: [Step5CopyVisibility.kt](src/main/kotlin/com/imayankkumar/roadmap/dataclasses/Step5CopyVisibility.kt) · Java: [Coupon.java](decompiled/com/imayankkumar/roadmap/dataclasses/Coupon.java), [SafeCoupon.java](decompiled/com/imayankkumar/roadmap/dataclasses/SafeCoupon.java)

```kotlin
@ExposedCopyVisibility
data class Coupon private constructor(val percent: Int) { /* of() allows 1..50 */ }

fun bypassCheck(): Coupon = Coupon.of(10).copy(percent = 90)   // works, 90%

@ConsistentCopyVisibility
data class SafeCoupon private constructor(val percent: Int) { /* same of() */ }
```

```java
// Coupon.java
private Coupon(int percent) { ... }
public final Coupon copy(int percent) { ... }

// SafeCoupon.java
private SafeCoupon(int percent) { ... }
private final SafeCoupon copy(int percent) { ... }
```

**What to notice:** the constructor is private in both, but only `SafeCoupon` makes `copy()` private too. With `Coupon`, outside code can skip the check in `of()`. The build prints a warning on `bypassCheck()` on purpose: Kotlin 2.4.21 says this "will become an error in language version 2.5". Without either annotation, the compiler warns on the class itself: "Non-public primary constructor is exposed via the generated 'copy()' method of the 'data' class."

## Won't compile

Paste these into the [playground](../../playground) (or into this topic's `src/main/kotlin`, where the file says so) to see the errors.

| File | Error |
| --- | --- |
| [Step3OpenDataClass.kt.txt](wont-compile/Step3OpenDataClass.kt.txt) | Modifier 'open' is incompatible with 'data'. |
| [Step3NoConstructorProperty.kt.txt](wont-compile/Step3NoConstructorProperty.kt.txt) | Primary constructor of data class must only have property ('val' / 'var') parameters. |
| [Step3EmptyConstructor.kt.txt](wont-compile/Step3EmptyConstructor.kt.txt) | Data class must have at least one primary constructor parameter. |
| [Step5PrivateCopy.kt.txt](wont-compile/Step5PrivateCopy.kt.txt) | Cannot access 'fun copy(percent: Int = ...): SafeCoupon': it is private in 'com.imayankkumar.roadmap.dataclasses.SafeCoupon'. |

Errors checked with Kotlin 2.4.21.

## Try this

1. Move `isSeen` into the constructor of `Order`. Which test fails now, and why?
2. Add a `val tags: MutableList<String>` to `CartItem` and change the list after putting the item in a `HashSet`. Does `set.contains(item)` still find it?
3. Remove `@ExposedCopyVisibility` from `Coupon` and build. Read the warning, then switch it to `@ConsistentCopyVisibility`. What stops compiling?

## Further reading

- [Data classes](https://kotlinlang.org/docs/data-classes.html) on kotlinlang.org
- [KT-11914: copy() exposes a non-public constructor](https://youtrack.jetbrains.com/issue/KT-11914)
