pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Downloads JDK 21 if it isn't installed, so `./gradlew` works on any machine.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

rootProject.name = "android-roadmap"

include(":playground")

// Every folder in topics/ with a build file is a module named after the folder,
// which is also its topic id on imayankkumar.com/roadmap/.
file("topics").listFiles()
    ?.filter { it.resolve("build.gradle.kts").exists() }
    ?.sortedBy { it.name }
    ?.forEach { dir ->
        include(":${dir.name}")
        project(":${dir.name}").projectDir = dir
    }
