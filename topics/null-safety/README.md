# Kotlin null safety

> `String` can't be null, `String?` can. `?.`, `?:` and a null check keep the null case visible. `!!` moves the crash from build time to runtime. The gap is Java: values without annotations are platform types, so check them at the boundary or annotate the Java side.

**Read:** [How does null safety work in Kotlin?](https://imayankkumar.com/blog/kotlin-null-safety/) · **Practice:** [Interview questions](https://imayankkumar.com/interview/kotlin-null-safety/)

## Files

| Step | Post section | Kotlin | Java the compiler makes |
| --- | --- | --- | --- |
| 1 | How does Kotlin stop null crashes? | [Step1NullableTypes.kt](src/main/kotlin/com/imayankkumar/roadmap/nullsafety/Step1NullableTypes.kt) | [Step1NullableTypesKt.java](decompiled/com/imayankkumar/roadmap/nullsafety/Step1NullableTypesKt.java) |
| 2 | How do you use a value that might be null? | [Step2NullTools.kt](src/main/kotlin/com/imayankkumar/roadmap/nullsafety/Step2NullTools.kt) | [Step2NullToolsKt.java](decompiled/com/imayankkumar/roadmap/nullsafety/Step2NullToolsKt.java) |
| 3 | Why is !! risky? | [Step3NotNullAssertion.kt](src/main/kotlin/com/imayankkumar/roadmap/nullsafety/Step3NotNullAssertion.kt) | [Step3NotNullAssertionKt.java](decompiled/com/imayankkumar/roadmap/nullsafety/Step3NotNullAssertionKt.java) |
| 4 | Where can a NullPointerException still happen? | [Step4JavaInterop.kt](src/main/kotlin/com/imayankkumar/roadmap/nullsafety/Step4JavaInterop.kt), [CouponService.java](src/main/java/com/imayankkumar/roadmap/nullsafety/CouponService.java), [AnnotatedCouponService.java](src/main/java/com/imayankkumar/roadmap/nullsafety/AnnotatedCouponService.java) | [Step4JavaInteropKt.java](decompiled/com/imayankkumar/roadmap/nullsafety/Step4JavaInteropKt.java) |

Run [Main.kt](src/main/kotlin/com/imayankkumar/roadmap/nullsafety/Main.kt) to see every step's output. The tests in [NullSafetyTest.kt](src/test/kotlin/com/imayankkumar/roadmap/nullsafety/NullSafetyTest.kt) prove each claim.

## What the decompiled Java shows

- **`!!` is a method call.** `order!!.id` becomes `Intrinsics.checkNotNull(order)`, which throws `NullPointerException` if `order` is null.
- **Kotlin checks platform values for you.** Putting a Java `String!` into a Kotlin `String` adds `Intrinsics.checkNotNullExpressionValue(value, "getCouponCode(...)")`. That's why the crash happens on the assignment line, with the message `getCouponCode(...) must not be null`.
- **Public functions check their parameters.** Every public function with a non-null parameter starts with `Intrinsics.checkNotNullParameter(...)`, because Java callers can still pass null.
- **`?.` and `?:` are plain `if` checks.** No extra objects and no exceptions.
- **Annotated Java needs no runtime check.** `AnnotatedCouponService` is read as `String?`, so the compiler makes you handle null and adds no check.

## Won't compile

| File | Error |
| --- | --- |
| [Step1NullToNonNull.kt.txt](wont-compile/Step1NullToNonNull.kt.txt) | Null cannot be a value of a non-null type 'String'. |
| [Step1MemberOnNullable.kt.txt](wont-compile/Step1MemberOnNullable.kt.txt) | Only safe (?.) or non-null asserted (!!.) calls are allowed on a nullable receiver of type 'String?'. |
| [Step4AnnotatedJavaToNonNull.kt.txt](wont-compile/Step4AnnotatedJavaToNonNull.kt.txt) | Initializer type mismatch: expected 'String', actual 'String?'. |

Errors checked with Kotlin 2.4.21.

## Try this

1. Remove `@Nullable` from `AnnotatedCouponService.getCouponCode`, then run `./gradlew :null-safety:decompile`. What changes in `Step4JavaInteropKt.java`?
2. Make `findOrder` `internal` and decompile again. Does the parameter check stay?

## Further reading

- [Null safety](https://kotlinlang.org/docs/null-safety.html) on kotlinlang.org
- [Calling Java from Kotlin: nullability annotations](https://kotlinlang.org/docs/java-interop.html#nullability-annotations)
