rootProject.name = "boxdrawer"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven( "https://s01.oss.sonatype.org/content/repositories/releases/")
    }

//    plugins {
//        alias(libs.plugins.jetbrainsCompose) apply false
//        alias(libs.plugins.kotlinMultiplatform) apply false
//
////        kotlin("multiplatform").version(extra["kotlin.version"] as String)
////        id("org.jetbrains.compose").version(extra["compose.version"] as String)
//    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven( "https://jitpack.io" )
    }
}

include(":dxfprinter")
include(":figure")
include(":commonMain")
