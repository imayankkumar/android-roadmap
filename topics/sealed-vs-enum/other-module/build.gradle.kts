// A second module, to show which sealed-type rules change across a module boundary.
plugins {
    id("roadmap.topic")
}

dependencies {
    implementation(project(":sealed-vs-enum"))
}
