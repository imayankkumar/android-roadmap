plugins {
    id("roadmap.topic")
}

dependencies {
    // Nullability annotations for the Java side of step 4.
    implementation(libs.jspecify)
}
