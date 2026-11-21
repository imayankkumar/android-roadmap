# Inner vs nested classes in Kotlin

> A nested class is just a class written inside another class. It has no link to any outer object. Mark it `inner` and every instance keeps a hidden reference to the outer object. That lets it use the outer class's members, but it also keeps the outer object alive for as long as the inner one lives.

**Read:** [Inner vs nested class in Kotlin: what is the difference?](https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/) · **Practice:** [Interview questions](https://imayankkumar.com/interview/kotlin-inner-vs-nested-class/)

Run [Main.kt](src/main/kotlin/com/imayankkumar/roadmap/innervsnestedclasses/Main.kt) to see what every step prints. Each step below shows the key Kotlin lines next to what the compiler makes from them. The tests in [InnerVsNestedTest.kt](src/test/kotlin/com/imayankkumar/roadmap/innervsnestedclasses/InnerVsNestedTest.kt) prove each claim.

**A note on the Java files for this topic.** The decompiler rebuilds the hidden field into Java's `OrderTracker.this`, which hides the very thing this topic is about. So for steps 1 and 2 this README shows the raw bytecode instead. You get the same view with `./gradlew :inner-vs-nested-classes:bytecode`, or in Android Studio with **Tools > Kotlin > Show Kotlin Bytecode**.

## Step 1: nested vs inner

Post sections: [What is a nested class in Kotlin?](https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/#what-is-a-nested-class-in-kotlin) and [What changes when you add inner?](https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/#what-changes-when-you-add-inner) · Code: [Step1Nested.kt](src/main/kotlin/com/imayankkumar/roadmap/innervsnestedclasses/Step1Nested.kt) · Java: [OrderTracker.java](decompiled/com/imayankkumar/roadmap/innervsnestedclasses/OrderTracker.java)

```kotlin
class OrderTracker(val orderId: String) {

    class DeliveryStop(val name: String, val etaMinutes: Int)

    inner class StatusLabel {
        fun text() = "Order $orderId"
        fun tracker() = this@OrderTracker
    }
}

OrderTracker.DeliveryStop("Restaurant", 12)   // no OrderTracker needed
tracker.StatusLabel()                         // needs an OrderTracker
```

```text
public static final class OrderTracker$DeliveryStop {          // from OrderTracker.java
  public OrderTracker$DeliveryStop(String, int);
}

public final class OrderTracker$StatusLabel {                  // javap -c -p
  final OrderTracker this$0;

  public OrderTracker$StatusLabel(OrderTracker);
     1: aload_1
     2: putfield      // Field this$0
     6: invokespecial // Method java/lang/Object."<init>"

  public final OrderTracker tracker();
     1: getfield      // Field this$0
     4: areturn
}
```

**What to notice:** the nested class is `static`, and its constructor takes only its own parameters. The inner class has a field `this$0`, and its constructor takes the outer object first and stores it before anything else runs. `this@OrderTracker` is just a read of `this$0`. The field has no `public` or `private`, so it is package-private, and the tests show it is marked synthetic (made by the compiler).

## Step 2: what the compiler generates

Post section: [What does the compiler generate?](https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/#what-does-the-compiler-generate) · Code: [Step2Generated.kt](src/main/kotlin/com/imayankkumar/roadmap/innervsnestedclasses/Step2Generated.kt) · Java: [Bill.java](decompiled/com/imayankkumar/roadmap/innervsnestedclasses/Bill.java)

```kotlin
class Bill(private var total: Int) {
    inner class AddItem {
        fun add(price: Int) { total += price }
    }

    fun lambdaWithoutBill(): () -> String = { "no Bill here" }
    fun lambdaWithBill(): () -> String = { "total $total" }
}
```

```text
public final void add(int);                                  // Bill$AddItem, javap -c -p
   1: getfield      // Field this$0
   8: invokestatic  // Method Bill.access$getTotal$p
  13: invokestatic  // Method Bill.access$setTotal$p

private static final String lambdaWithoutBill$lambda$0();    // Bill.java
private static final String lambdaWithBill$lambda$0(Bill this$0);
```

**What to notice:** `total` is private, so the inner class can't touch it directly in bytecode. Kotlin adds two helper methods to `Bill`, `access$getTotal$p` and `access$setTotal$p`. They are still there with a Java 17 target. The lambdas show the same rule as the post's FAQ: only the lambda that uses `total` takes the `Bill`.

## Step 3: how an inner class leaks its screen

Post section: [How can an inner class leak an Activity?](https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/#how-can-an-inner-class-leak-an-activity) · Code: [Step3Leak.kt](src/main/kotlin/com/imayankkumar/roadmap/innervsnestedclasses/Step3Leak.kt) · Java: [TrackingScreenAlmostRight.java](decompiled/com/imayankkumar/roadmap/innervsnestedclasses/TrackingScreenAlmostRight.java), [TrackingScreenRight.java](decompiled/com/imayankkumar/roadmap/innervsnestedclasses/TrackingScreenRight.java)

This module is plain Kotlin, so `TrackingScreen` stands in for an Activity. The tests call `onCreate`, `onStart`, `onStop` and `onDestroy` by hand, like the system does when you rotate the phone.

```kotlin
// Almost right: added in onCreate, never removed
class TrackingScreenAlmostRight : TrackingScreen() {
    inner class StatusUpdater : StatusListener {
        override fun onStatus(status: String) { statusText = status }
    }
    override fun onCreate() { OrderUpdates.add(StatusUpdater()) }
}

// Right: still inner, but it lives only while the screen is visible
override fun onStart() { OrderUpdates.add(updater) }
override fun onStop() { OrderUpdates.remove(updater) }
```

```java
public void onCreate() {
    OrderUpdates.INSTANCE.add(new TrackingScreenAlmostRight.StatusUpdater());
}
```

**What to notice:** after one rotation, `OrderUpdates` holds two listeners. The test reads each listener's `this$0`, the way a heap dump would, and finds the destroyed screen still there. In the right version, only the live screen is held and the old one gets no updates. The class is still `inner` in both: what fixes it is the lifetime, not the keyword.

## Step 4: when to use inner

Post section: [When should you use inner?](https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/#when-should-you-use-inner) · Code: [Step4WhenInner.kt](src/main/kotlin/com/imayankkumar/roadmap/innervsnestedclasses/Step4WhenInner.kt) · Java: [MenuAdapter.java](decompiled/com/imayankkumar/roadmap/innervsnestedclasses/MenuAdapter.java), [OrderSummary.java](decompiled/com/imayankkumar/roadmap/innervsnestedclasses/OrderSummary.java)

```kotlin
class MenuAdapter(private val onClick: (String) -> Unit) {
    inner class ItemHolder(private val name: String) {   // lives and dies with the adapter
        fun click() = onClick(name)
    }
}

class OrderSummary(val orderId: String, val lines: List<String>) {
    class Header(private val orderId: String)            // gets one value, not the whole summary
}
```

**What to notice:** `ItemHolder` is `inner` because the holders never outlive their adapter. `Header` only needs `orderId`, so it is nested and gets that one value. The test checks that `Header` has no field pointing to `OrderSummary`.

## Won't compile

Paste these into the [playground](../../playground) (or into this topic's `src/main/kotlin`, where the file says so) to see the errors.

| File | Error |
| --- | --- |
| [Step1NestedReadsOuter.kt.txt](wont-compile/Step1NestedReadsOuter.kt.txt) | Outer class 'class OrderTracker : Any' of non-inner class cannot be used as receiver. |
| [Step1InnerWithoutOuter.kt.txt](wont-compile/Step1InnerWithoutOuter.kt.txt) | Constructor of the inner class 'inner class StatusLabel : Any' can only be called with a receiver of the containing class. |

Errors checked with Kotlin 2.4.21.

## Try this

1. Remove `inner` from `StatusLabel`. Which lines stop compiling, and what does the error say?
2. Make `total` in `Bill` `internal` instead of `private` and run `bytecode`. Are the `access$` methods still there?
3. In `TrackingScreenAlmostRight`, change `StatusUpdater` to a nested class that takes `statusText` in its constructor. Does the leak test still find the old screen? What would happen on Android, where a View holds its Activity?

## Further reading

- [Nested and inner classes](https://kotlinlang.org/docs/nested-classes.html) and [this expressions](https://kotlinlang.org/docs/this-expressions.html) on kotlinlang.org
- [HandlerLeak lint check](https://googlesamples.github.io/android-custom-lint-rules/checks/HandlerLeak.md.html)
