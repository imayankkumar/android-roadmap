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

## Topics

### Stage 1: Kotlin and DSA basics

| Topic | Post | Interview questions | Code |
| --- | --- | --- | --- |
| Null safety | [How does null safety work in Kotlin?](https://imayankkumar.com/blog/kotlin-null-safety/) | [Questions](https://imayankkumar.com/interview/kotlin-null-safety/) | [topics/null-safety](topics/null-safety) |

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

- Kotlin 2.4.21 on the JVM, JDK 21, Gradle with a version catalog.
- One Gradle module per topic. The folder name is the topic id used on the site.
- Decompiled Java comes from [Vineflower](https://github.com/Vineflower/vineflower). Kotlin's metadata annotations are removed so the code is easier to read.
- CI builds every topic, runs every test, and fails if `decompiled/` doesn't match the current Kotlin version. When Kotlin changes what it generates, the diff shows exactly what changed.

## About

I'm Mayank, an Android engineer focused on Kotlin, software architecture, performance engineering, and AI-assisted development. I write at [imayankkumar.com](https://imayankkumar.com).

Code is licensed under [Apache 2.0](LICENSE).
