# Kotlin null safety

> `String` can't be null, `String?` can. `?.`, `?:` and a null check keep the null case visible. `!!` moves the crash from build time to runtime. The gap is Java: values without annotations are platform types, so check them at the boundary or annotate the Java side.

**Read:** [How does null safety work in Kotlin?](https://imayankkumar.com/blog/kotlin-null-safety/) · **Practice:** [Interview questions](https://imayankkumar.com/interview/kotlin-null-safety/)

Run [Main.kt](src/main/kotlin/com/imayankkumar/roadmap/nullsafety/Main.kt) to see what every step prints. Each step below shows the key Kotlin lines next to the Java the compiler makes from them. The tests in [NullSafetyTest.kt](src/test/kotlin/com/imayankkumar/roadmap/nullsafety/NullSafetyTest.kt) prove each claim.

## Step 1: `String` vs `String?`

Post section: [How does Kotlin stop null crashes?](https://imayankkumar.com/blog/kotlin-null-safety/#how-does-kotlin-stop-null-crashes) · Code: [Step1NullableTypes.kt](src/main/kotlin/com/imayankkumar/roadmap/nullsafety/Step1NullableTypes.kt) · Java: [Step1NullableTypesKt.java](decompiled/com/imayankkumar/roadmap/nullsafety/Step1NullableTypesKt.java)

```kotlin
fun deliveryNoteLength(order: Order): Int {
    val note: String? = order.deliveryNote
    if (note != null) {
        return note.length          // smart cast: note is String here
    }
    return 0
}
```

```java
public static final int deliveryNoteLength(@NotNull Order order) {
    Intrinsics.checkNotNullParameter(order, "order");
    String note = order.getDeliveryNote();
    return note != null ? note.length() : 0;
}
```

**What to notice:** the smart cast costs nothing, it's a plain `!= null` check. The first line is new: Kotlin checks that a Java caller didn't pass null into a non-null parameter.

## Step 2: the four null tools

Post section: [How do you use a value that might be null?](https://imayankkumar.com/blog/kotlin-null-safety/#how-do-you-use-a-value-that-might-be-null) · Code: [Step2NullTools.kt](src/main/kotlin/com/imayankkumar/roadmap/nullsafety/Step2NullTools.kt) · Java: [Step2NullToolsKt.java](decompiled/com/imayankkumar/roadmap/nullsafety/Step2NullToolsKt.java)

```kotlin
fun riderPhone(order: Order?): String? = order?.rider?.phone

fun checkoutSummary(orderId: String): String {
    val order = findOrder(orderId) ?: return "Order $orderId not found"
    return "Order ${order.id}: ₹${order.total}"
}
```

```java
public static final String riderPhone(@Nullable Order order) {
    if (order != null) {
        Rider var10000 = order.getRider();
        if (var10000 != null) {
            return var10000.getPhone();
        }
    }
    return null;
}

public static final String checkoutSummary(@NotNull String orderId) {
    Intrinsics.checkNotNullParameter(orderId, "orderId");
    Order var10000 = ModelKt.findOrder(orderId);
    if (var10000 == null) {
        return "Order " + orderId + " not found";
    }
    Order order = var10000;
    return "Order " + order.getId() + ": ₹" + order.getTotal();
}
```

**What to notice:** `?.` and `?:` become the same `if` checks you'd write by hand in Java. No extra objects, no exceptions.

## Step 3: the not-null assertion `!!`

Post section: [Why is !! risky?](https://imayankkumar.com/blog/kotlin-null-safety/#why-is--risky) · Code: [Step3NotNullAssertion.kt](src/main/kotlin/com/imayankkumar/roadmap/nullsafety/Step3NotNullAssertion.kt) · Java: [Step3NotNullAssertionKt.java](decompiled/com/imayankkumar/roadmap/nullsafety/Step3NotNullAssertionKt.java)

```kotlin
fun orderTitleAlmostRight(orderId: String): String {
    val order = findOrder(orderId)
    return "Order ${order!!.id} · ₹${order.total}"
}
```

```java
public static final String orderTitleAlmostRight(@NotNull String orderId) {
    Intrinsics.checkNotNullParameter(orderId, "orderId");
    Order order = ModelKt.findOrder(orderId);
    Intrinsics.checkNotNull(order);
    return "Order " + order.getId() + " · ₹" + order.getTotal();
}
```

**What to notice:** `!!` is a method call, `Intrinsics.checkNotNull`, that throws `NullPointerException` when the value is null. The compiler doesn't make it safe, it only moves the check to runtime.

## Step 4: nulls from Java

Post section: [Where can a NullPointerException still happen in Kotlin?](https://imayankkumar.com/blog/kotlin-null-safety/#where-can-a-nullpointerexception-still-happen-in-kotlin) · Code: [Step4JavaInterop.kt](src/main/kotlin/com/imayankkumar/roadmap/nullsafety/Step4JavaInterop.kt), [CouponService.java](src/main/java/com/imayankkumar/roadmap/nullsafety/CouponService.java), [AnnotatedCouponService.java](src/main/java/com/imayankkumar/roadmap/nullsafety/AnnotatedCouponService.java) · Java: [Step4JavaInteropKt.java](decompiled/com/imayankkumar/roadmap/nullsafety/Step4JavaInteropKt.java)

```kotlin
fun couponCrashes(orderId: String): String {
    val code: String = CouponService().getCouponCode(orderId)   // Java returns String!
    return code
}

fun couponFromAnnotated(orderId: String): String =
    AnnotatedCouponService().getCouponCode(orderId) ?: "No coupon" // @Nullable, seen as String?
```

```java
public static final String couponCrashes(@NotNull String orderId) {
    Intrinsics.checkNotNullParameter(orderId, "orderId");
    String var10000 = new CouponService().getCouponCode(orderId);
    Intrinsics.checkNotNullExpressionValue(var10000, "getCouponCode(...)");
    return var10000;
}

public static final String couponFromAnnotated(@NotNull String orderId) {
    Intrinsics.checkNotNullParameter(orderId, "orderId");
    String var10000 = new AnnotatedCouponService().getCouponCode(orderId);
    if (var10000 == null) {
        var10000 = "No coupon";
    }
    return var10000;
}
```

**What to notice:** when a Java value goes into a Kotlin `String`, Kotlin adds `checkNotNullExpressionValue`. That's why the crash happens on the assignment line, with the message `getCouponCode(...) must not be null`. With `@Nullable` on the Java side, the compiler makes you handle null, so no runtime check is needed.

## Won't compile

Paste these into the [playground](../../playground) to see the errors.

| File | Error |
| --- | --- |
| [Step1NullToNonNull.kt.txt](wont-compile/Step1NullToNonNull.kt.txt) | Null cannot be a value of a non-null type 'String'. |
| [Step1MemberOnNullable.kt.txt](wont-compile/Step1MemberOnNullable.kt.txt) | Only safe (?.) or non-null asserted (!!.) calls are allowed on a nullable receiver of type 'String?'. |
| [Step4AnnotatedJavaToNonNull.kt.txt](wont-compile/Step4AnnotatedJavaToNonNull.kt.txt) | Initializer type mismatch: expected 'String', actual 'String?'. |

Errors checked with Kotlin 2.4.21.

## Try this

1. Remove `@Nullable` from `AnnotatedCouponService.getCouponCode`, then run `./gradlew :null-safety:decompile`. What changes in `Step4JavaInteropKt.java`?
2. Make `deliveryNoteLength` `private` and decompile again. Does the parameter check stay?

## Further reading

- [Null safety](https://kotlinlang.org/docs/null-safety.html) on kotlinlang.org
- [Calling Java from Kotlin: nullability annotations](https://kotlinlang.org/docs/java-interop.html#nullability-annotations)
