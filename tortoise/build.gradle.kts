@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("java-library")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.locationtech.jts:jts-core:1.19.0")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation(project(":vectors"))
    implementation(project(":segments"))
    implementation(project(":figure"))
    implementation(libs.kotlin.serialization.json)
    implementation("com.google.code.gson:gson:2.9.1")
    implementation(project(":dxfprinter"))
}