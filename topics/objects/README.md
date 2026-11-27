# Objects in Kotlin

> A class is a blueprint: every constructor call gives you a new instance. An `object` is a class with exactly one instance. Kotlin creates it the first time you use it, and you reach it by its name. `object : Listener { }` is different: it makes a new, unnamed instance every time the code runs. A `data object` is an object that prints its name.

**Read:** [What is an object in Kotlin, and how is it different from a class?](https://imayankkumar.com/blog/kotlin-object/) · **Practice:** [Interview questions](https://imayankkumar.com/interview/kotlin-object/)

Run [Main.kt](src/main/kotlin/com/imayankkumar/roadmap/objects/Main.kt) to see what every step prints. Each step below shows the key Kotlin lines next to the Java the compiler makes from them. The tests in [ObjectTest.kt](src/test/kotlin/com/imayankkumar/roadmap/objects/ObjectTest.kt) prove each claim.

## Step 1: an object declaration

Post section: [What is an object in Kotlin?](https://imayankkumar.com/blog/kotlin-object/#what-is-an-object-in-kotlin) · Code: [Step1ObjectDeclaration.kt](src/main/kotlin/com/imayankkumar/roadmap/objects/Step1ObjectDeclaration.kt) · Java: [Step1ObjectDeclarationKt.java](decompiled/com/imayankkumar/roadmap/objects/Step1ObjectDeclarationKt.java)

```kotlin
class SessionClass {
    var email: String? = null
}

object Session {
    var email: String? = null
}

SessionClass().email = "hello@imayankkumar.com"
SessionClass().email // null

Session.email = "hello@imayankkumar.com"
Session.email        // hello@imayankkumar.com
```

```java
new SessionClass().setEmail("hello@imayankkumar.com");
return new SessionClass().getEmail();

Session.INSTANCE.setEmail("hello@imayankkumar.com");
return Session.INSTANCE.getEmail();
```

**What to notice:** every `SessionClass()` is a `new`, so the second screen reads an empty instance. `Session` has no `new` at all: both lines go through the same `Session.INSTANCE`. An object can't have a constructor or live inside a function (see [wont-compile](#wont-compile)).

## Step 2: what the compiler makes

Post section: [What does the compiler turn it into?](https://imayankkumar.com/blog/kotlin-object/#what-does-the-compiler-turn-it-into) · Code: [Step2WhatTheCompilerMakes.kt](src/main/kotlin/com/imayankkumar/roadmap/objects/Step2WhatTheCompilerMakes.kt) · Java: [Session.java](decompiled/com/imayankkumar/roadmap/objects/Session.java)

```kotlin
object Session {
    var email: String? = null

    init {
        println("Session created")
    }

    fun isLoggedIn(): Boolean = email != null
}

fun checkLogin(): Boolean = Session.isLoggedIn()
```

```java
public final class Session {
    public static final Session INSTANCE = new Session();
    private static String email;

    private Session() {
    }

    public final boolean isLoggedIn() {
        return email != null;
    }

    static {
        System.out.println("Session created");
    }
}

public static final boolean checkLogin() {
    return Session.INSTANCE.isLoggedIn();
}
```

**What to notice:** the one instance is the static field `INSTANCE`, and the constructor is private, so no other code can make a second one. The `init` block moved into the `static` block. `Session.isLoggedIn()` became `Session.INSTANCE.isLoggedIn()`: the function runs on a real instance, which is why an object can implement interfaces.

## Step 3: when it is created

Post section: [When is it created, and is it thread-safe?](https://imayankkumar.com/blog/kotlin-object/#when-is-it-created-and-is-it-thread-safe) · Code: [Step3WhenItIsCreated.kt](src/main/kotlin/com/imayankkumar/roadmap/objects/Step3WhenItIsCreated.kt) · Java: [AppConfig.java](decompiled/com/imayankkumar/roadmap/objects/AppConfig.java), [BuildInfo.java](decompiled/com/imayankkumar/roadmap/objects/BuildInfo.java)

```kotlin
object AppConfig {
    val first = log("AppConfig: first property")

    init {
        log("AppConfig: init block")
    }

    val second = log("AppConfig: second property")
}

object BuildInfo {
    const val VERSION = "2.4"
    val createdAt = log("BuildInfo: created")
}
```

```java
public final class AppConfig {
    public static final AppConfig INSTANCE = new AppConfig();
    private static final String first = Step3WhenItIsCreatedKt.access$log("AppConfig: first property");
    private static final String second = Step3WhenItIsCreatedKt.access$log("AppConfig: second property");

    static {
        Step3WhenItIsCreatedKt.access$log("AppConfig: init block");
    }
}
```

**What to notice:** the Java makes it look like `init` runs last, but that is how the decompiler prints field initializers. Run `./gradlew :objects:bytecode` and read `static {}`: it sets `first`, runs the `init` code, then sets `second`, in the order you wrote them. The test checks the real order. Reading `BuildInfo.VERSION` doesn't create `BuildInfo`, because the compiler copies `"2.4"` into the caller. Eight threads that touch `Registry` at the same moment all get one instance, and its `init` runs once. Only creation is thread-safe, not the `var count` inside `Hits`. Main.kt runs 8 threads × 10,000 increments, and the plain `var` can end up below 80,000, while the `AtomicInteger` always reaches it.

## Step 4: object expressions

Post section: [What is an object expression?](https://imayankkumar.com/blog/kotlin-object/#what-is-an-object-expression) · Code: [Step4ObjectExpression.kt](src/main/kotlin/com/imayankkumar/roadmap/objects/Step4ObjectExpression.kt) · Java: [Step4ObjectExpressionKt.java](decompiled/com/imayankkumar/roadmap/objects/Step4ObjectExpressionKt.java)

```kotlin
// Almost right: this removes nothing
button.addLoginListener(object : LoginListener { /* ... */ })
button.removeLoginListener(object : LoginListener { /* ... */ })

// An object expression can change a local var
var count = 0
button.addLoginListener(object : LoginListener {
    override fun onSuccess(email: String) { count++ }
    override fun onError(message: String) {}
})
```

```java
button.addLoginListener(new LoginListener() { ... });
button.removeLoginListener(new LoginListener() { ... });

final IntRef count = new IntRef();
button.addLoginListener(new LoginListener() {
    public void onSuccess(String email) {
        int var2 = count.element++;
    }
    ...
});
```

**What to notice:** each `object :` is a `new`, so the remove call gets a different object and the first listener stays. [LoginScreen](decompiled/com/imayankkumar/roadmap/objects/LoginScreen.java) keeps one reference in a field and uses it in `onStart()` and `onStop()`, and the count goes back to 0. The local `var count` becomes an `IntRef` box, so the object and the function share it.

## Step 5: data objects

Post section: [What is a data object?](https://imayankkumar.com/blog/kotlin-object/#what-is-a-data-object) · Code: [Step5DataObject.kt](src/main/kotlin/com/imayankkumar/roadmap/objects/Step5DataObject.kt) · Java: [AuthState.java](decompiled/com/imayankkumar/roadmap/objects/AuthState.java)

```kotlin
sealed interface AuthState {
    data object LoggedOut : AuthState
    data class LoggedIn(val email: String) : AuthState
}
```

```java
final class LoggedOut implements AuthState {
    public static final AuthState.LoggedOut INSTANCE = new AuthState.LoggedOut();

    public String toString() {
        return "LoggedOut";
    }

    public int hashCode() {
        return -1412663412;
    }

    public boolean equals(Object other) {
        return this == other ? true : other instanceof AuthState.LoggedOut;
    }
}
```

**What to notice:** `toString()` is the name, the hash is a fixed number, and `equals()` checks the type. The test makes a second `LoggedOut` with reflection: `==` says equal, `===` says not the same. A plain object made twice is not equal, and it prints `PlainAuthState$LoggedOut@` and a hash. There is no `copy()` or `component1()`, unlike `LoggedIn`.

## Won't compile

Paste these into the [playground](../../playground) to see the errors.

| File | Error |
| --- | --- |
| [Step1LocalObject.kt.txt](wont-compile/Step1LocalObject.kt.txt) | Named object 'Inside' cannot be local. Try to use an anonymous object instead. |
| [Step1ObjectConstructor.kt.txt](wont-compile/Step1ObjectConstructor.kt.txt) | Objects cannot have constructors. |
| [Step4PublicAnonymousObject.kt.txt](wont-compile/Step4PublicAnonymousObject.kt.txt) | Unresolved reference 'x' on receiver of type 'Any'. |
| [Step5DataObjectEquals.kt.txt](wont-compile/Step5DataObjectEquals.kt.txt) | Data object cannot have a custom implementation of 'equals' or 'hashCode'. |

Errors checked with Kotlin 2.4.21.

## Try this

1. Make `point()` in [Step4PublicAnonymousObject.kt.txt](wont-compile/Step4PublicAnonymousObject.kt.txt) `private`. Does `point().x` compile now? Why?
2. Change `const val VERSION` in `BuildInfo` to a plain `val`. Which test fails, and what changed in `BuildInfo.java`?
3. Remove `data` from `AuthState.LoggedOut` and run `decompile`. Which three functions disappear?

## Further reading

- [Object declarations and expressions](https://kotlinlang.org/docs/object-declarations.html) on kotlinlang.org
- [Stable data objects](https://kotlinlang.org/docs/whatsnew19.html#stable-data-objects-for-symmetry-with-data-classes) in What's new in Kotlin 1.9.0
