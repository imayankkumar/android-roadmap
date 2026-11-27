# Value classes in Kotlin

> A value class wraps one value and gives it its own type, like `Email` instead of a plain `String`. The compiler checks the type, but at runtime Kotlin passes the plain `String`, so no extra object is created. It creates a real object only when the value is used as another type, like `Any`, a generic type or an item in a list.

**Read:** [What is a value class in Kotlin, and when does it create an object?](https://imayankkumar.com/blog/kotlin-value-class/) · **Practice:** [Interview questions](https://imayankkumar.com/interview/kotlin-value-class/)

Run [Main.kt](src/main/kotlin/com/imayankkumar/roadmap/valueclasses/Main.kt) to see what every step prints. Each step below shows the key Kotlin lines next to the Java the compiler makes from them. The tests in [ValueClassTest.kt](src/test/kotlin/com/imayankkumar/roadmap/valueclasses/ValueClassTest.kt) prove each claim.

## Step 1: a value class

Post section: [What is a value class in Kotlin?](https://imayankkumar.com/blog/kotlin-value-class/#what-is-a-value-class-in-kotlin) · Code: [Step1ValueClass.kt](src/main/kotlin/com/imayankkumar/roadmap/valueclasses/Step1ValueClass.kt) · Java: [Email.java](decompiled/com/imayankkumar/roadmap/valueclasses/Email.java)

```kotlin
@JvmInline
value class Email(val value: String) {
    init {
        require("@" in value) { "Not an email: $value" }
    }
}

@JvmInline
value class PhoneNumber(val value: String)
```

```java
public final class Email {
    private final String value;

    public static String constructor-impl(String value) {
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Not an email: " + value);
        }
        return value;
    }

    public static boolean equals-impl0(String p1, String p2) { ... }
    public static String toString-impl(String arg0) { ... }
}
```

**What to notice:** the `Email` class still exists, but your `init` block moves into a static `constructor-impl` that checks the `String` and returns it. Most of the class is static functions that take the plain `String`. Swap the email and phone number and the build fails (see [Step1SwappedArguments.kt.txt](wont-compile/Step1SwappedArguments.kt.txt)).

## Step 2: typealias or normal class

Post section: [Why not use a typealias or a normal class?](https://imayankkumar.com/blog/kotlin-value-class/#why-not-use-a-typealias-or-a-normal-class) · Code: [Step2TypealiasOrClass.kt](src/main/kotlin/com/imayankkumar/roadmap/valueclasses/Step2TypealiasOrClass.kt) · Java: [Step2TypealiasOrClassKt.java](decompiled/com/imayankkumar/roadmap/valueclasses/Step2TypealiasOrClassKt.java)

```kotlin
typealias EmailAlias = String

fun sendOtpWithAlias(email: EmailAlias, phone: String): String = "Sending OTP to $email and $phone"

// Compiles: to the compiler, EmailAlias and String are the same type.
fun swapWithAliasCompiles(email: EmailAlias, phone: String): String = sendOtpWithAlias(phone, email)
```

```java
public static final String sendOtpWithAlias(String email, String phone) { ... }
```

**What to notice:** the typealias is gone from the Java. It was only a second name for `String`, so the swap compiles and the test shows the wrong order going through. `EmailObject` is a normal class: it is a real type, but every instance is a new object.

## Step 3: what the compiler makes

Post section: [What does the compiler turn it into?](https://imayankkumar.com/blog/kotlin-value-class/#what-does-the-compiler-turn-it-into) · Code: [Step3WhatTheCompilerMakes.kt](src/main/kotlin/com/imayankkumar/roadmap/valueclasses/Step3WhatTheCompilerMakes.kt) · Java: [Step3WhatTheCompilerMakesKt.java](decompiled/com/imayankkumar/roadmap/valueclasses/Step3WhatTheCompilerMakesKt.java)

```kotlin
fun sendOtp(email: Email, phone: PhoneNumber) {
    println("Sending OTP to ${email.value} and ${phone.value}")
}

fun createAndSend() {
    val email = Email("asha@example.com")
    val phone = PhoneNumber("+91 98765 43210")
    sendOtp(email, phone)
}
```

```java
public static final void sendOtp-JkvqDOU(String email, String phone) {
    System.out.println("Sending OTP to " + email + " and " + phone);
}

public static final void createAndSend() {
    String email = Email.constructor-impl("asha@example.com");
    String phone = PhoneNumber.constructor-impl("+91 98765 43210");
    sendOtp-JkvqDOU(email, phone);
}
```

**What to notice:** there is no `new Email(...)`. Both variables are plain `String` values, and `sendOtp` takes two strings. Its name gets a hash, `-JkvqDOU`, so it can't clash with a `sendOtp(String, String)`. The decompiler prints the names as `sendOtp_JkvqDOU` with a `$VF was:` comment, because `-` isn't allowed in Java names.

## Step 4: when it creates an object

Post section: [When does a value class create an object?](https://imayankkumar.com/blog/kotlin-value-class/#when-does-a-value-class-create-an-object) · Code: [Step4WhenItCreatesAnObject.kt](src/main/kotlin/com/imayankkumar/roadmap/valueclasses/Step4WhenItCreatesAnObject.kt) · Java: [Step4WhenItCreatesAnObjectKt.java](decompiled/com/imayankkumar/roadmap/valueclasses/Step4WhenItCreatesAnObjectKt.java)

| Function | Java parameter or call | Object created |
| --- | --- | --- |
| `asParameter(email: Email): Email` | `String asParameter-G7BZY3w(String email)` | No |
| `class User(val email: Email)` | `private final String email` | No |
| `isSame(a, b)` with `==` | `Email.equals-impl0(a, b)` | No |
| `asText(email)` with `"$email"` | `Email.toString-impl(email)` | No |
| `nullableEmail(email: Email?)` | `String email` | No |
| `asAny(email): Any` | `return Email.box-impl(email)` | Yes |
| `genericEmail(email)` | `asGeneric(Email.box-impl(email))` | Yes |
| `inList(email)` | `listOf(Email.box-impl(email))` | Yes |
| `inLambda(email, action)` | `action.invoke(Email.box-impl(email))` | Yes |
| `nullableAge(age: Age?)` | `Age age` | Yes |

**What to notice:** every "Yes" row has `box-impl`, which is the one place a real `Email` object is made. `Email.java` doesn't list `box-impl` because the compiler marks it as generated, so the decompiler hides it; `./gradlew :value-classes:bytecode` shows it. `Email?` stays a `String` because a `String` can already be `null`. `Age` wraps an `Int`, and an `int` can't be `null`, so `Age?` has to be an object.

## Step 5: calling from Java

Post section: [Frequently asked questions](https://imayankkumar.com/blog/kotlin-value-class/#frequently-asked-questions) · Code: [Step5CallingFromJava.kt](src/main/kotlin/com/imayankkumar/roadmap/valueclasses/Step5CallingFromJava.kt) · Java: [Step5CallingFromJavaKt.java](decompiled/com/imayankkumar/roadmap/valueclasses/Step5CallingFromJavaKt.java)

```kotlin
@JvmName("sendOtpJava")
fun sendOtpFromJava(email: Email, phone: PhoneNumber): String
```

```java
public static final String sendOtpJava(String email, String phone)
```

**What to notice:** Java can't call `sendOtp-JkvqDOU`, but it can call `sendOtpJava`. It still takes two plain strings, so the `Email` type check only happens on the Kotlin side.

## Won't compile

Paste these into the [playground](../../playground) to see the errors.

| File | Error |
| --- | --- |
| [Step1SwappedArguments.kt.txt](wont-compile/Step1SwappedArguments.kt.txt) | Argument type mismatch: actual type is 'PhoneNumber', but 'Email' was expected. |
| [Step1TwoValues.kt.txt](wont-compile/Step1TwoValues.kt.txt) | Value class must have exactly one primary constructor parameter. |
| [Step1IdentityCheck.kt.txt](wont-compile/Step1IdentityCheck.kt.txt) | Identity equality for arguments of types 'Email' and 'Email' is prohibited. |
| [Step1MissingJvmInline.kt.txt](wont-compile/Step1MissingJvmInline.kt.txt) | Value classes without '@JvmInline' annotation are not yet supported. |

Errors checked with Kotlin 2.4.21.

## Try this

1. Make `Email` implement an interface, add `fun asContact(email: Email): Contact = email`, and run `decompile`. Do you find `box-impl`?
2. Change `inLambda` to an `inline fun`. Does `box-impl` disappear from the Java?
3. Run `./gradlew :value-classes:bytecode` and find `box-impl` and `unbox-impl` in `Email`. Which functions call them?

## Further reading

- [Inline value classes](https://kotlinlang.org/docs/inline-classes.html) on kotlinlang.org
- [Inline classes](https://kotlinlang.org/docs/whatsnew15.html#inline-classes) in What's new in Kotlin 1.5.0
