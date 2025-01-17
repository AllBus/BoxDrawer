@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("java-library")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets {
    dependencies {
        implementation(project(":figure"))
        implementation(files("libs/miethxml-ui.jar"))
        implementation("org.openpnp:opencv:4.9.0-0")
    }
}