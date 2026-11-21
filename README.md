# Android Roadmap: the code

Runnable Kotlin and Android code for every topic on the [Android roadmap](https://imayankkumar.com/roadmap/), with the Java the compiler makes from it.

Each post on [imayankkumar.com](https://imayankkumar.com/blog/) explains one question. This repo is where you run the examples, change them, and check every claim yourself.

## What's in each topic

| Folder | What it holds |
| --- | --- |
| `src/main/kotlin` | One file per section of the post, plus a `Main.kt` you can run |
| `src/test/kotlin` | One test for each claim in the post |
| `decompiled/` | The same code as readable Java, so you can see what Kotlin generates |
| `wont-compile/` | Snippets from the post that must fail to build, with the exact error |
| `README.md` | The short answer, links, and what each file shows |

## How to read a topic

Every topic folder follows the same path:

1. **Open the topic's README.** It starts with the short answer, then walks through each step with the key Kotlin lines next to the Java the compiler makes.
2. **Run `Main.kt`** to see what every step prints.
3. **Open a step file** (`Step1...kt`) when you want the full code. Its first lines link to the matching section of the post.
4. **Check the test** for that step. Each claim in the post is one test you can run.
5. **Open `decompiled/`** for the full Java of any file.
6. **Try `wont-compile/`.** Paste a snippet into the [playground](playground) and read the compiler error.
7. **Do the "Try this" tasks** at the end of the README. Change something, run `decompile` again, and compare.

## Topics

### Stage 1: Kotlin and DSA basics

| Topic | Post | Interview questions | Code |
| --- | --- | --- | --- |
| Null safety | [How does null safety work in Kotlin?](https://imayankkumar.com/blog/kotlin-null-safety/) | [Questions](https://imayankkumar.com/interview/kotlin-null-safety/) | [topics/null-safety](topics/null-safety) |
| Any, Unit and Nothing | [What do Any, Unit and Nothing mean in Kotlin?](https://imayankkumar.com/blog/kotlin-any-unit-nothing/) | [Questions](https://imayankkumar.com/interview/kotlin-any-unit-nothing/) | [topics/any-unit-nothing](topics/any-unit-nothing) |
| Data classes | [When should a Kotlin class be a data class?](https://imayankkumar.com/blog/kotlin-data-class/) | [Questions](https://imayankkumar.com/interview/kotlin-data-class/) | [topics/data-classes](topics/data-classes) |
| Sealed classes | [When should you use a sealed class in Kotlin?](https://imayankkumar.com/blog/kotlin-sealed-class/) | [Questions](https://imayankkumar.com/interview/kotlin-sealed-class/) | [topics/sealed-vs-enum](topics/sealed-vs-enum) |
| Inner vs nested classes | [Inner vs nested class in Kotlin: what is the difference?](https://imayankkumar.com/blog/kotlin-inner-vs-nested-class/) | [Questions](https://imayankkumar.com/interview/kotlin-inner-vs-nested-class/) | [topics/inner-vs-nested-classes](topics/inner-vs-nested-classes) |
| Enum classes | [When should you use an enum class in Kotlin?](https://imayankkumar.com/blog/kotlin-enum-class/) | [Questions](https://imayankkumar.com/interview/kotlin-enum-class/) | [topics/enum-classes](topics/enum-classes) |

More topics are added as their posts go live.

## Run it

You need JDK 21 or newer. Android Studio and IntelliJ IDEA both work; open the root folder.

```bash
./gradlew test                       # run every test
./gradlew :null-safety:test          # run one topic
./gradlew :null-safety:decompile     # write readable Java to topics/null-safety/decompiled/
./gradlew :null-safety:bytecode      # print the raw bytecode (javap -c -p)
```

In the IDE you can also open any Kotlin file and use **Tools > Kotlin > Show Kotlin Bytecode**, then **Decompile**.

Want to try something of your own? Use the [`playground`](playground) module. It's not tested and nothing there needs to stay in sync with the posts.

## How it's built

- Kotlin 2.4.21 on the JVM, built with JDK 21. The bytecode targets Java 17, like most Android apps, so the decompiled Java matches what an app ships.
- One Gradle module per topic. The folder name is the topic id used on the site.
- Decompiled Java comes from [Vineflower](https://github.com/Vineflower/vineflower), an open-source fork of the decompiler inside Android Studio, so it can run from Gradle and CI. Kotlin's metadata annotations are removed so the code is easier to read.
- CI builds every topic, runs every test, and fails if `decompiled/` doesn't match the current Kotlin version. When Kotlin changes what it generates, the diff shows exactly what changed.

## About

I'm Mayank, an Android engineer focused on Kotlin, software architecture, performance engineering, and AI-assisted development. I write at [imayankkumar.com](https://imayankkumar.com).

Code is licensed under [Apache 2.0](LICENSE).
