plugins {
    id("roadmap.topic")
}

dependencies {
    // StateFlow, for the shallow copy bug in step 4.
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}
