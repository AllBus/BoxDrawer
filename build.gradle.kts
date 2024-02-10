import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    // kotlin("multiplatform")
    //  id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.21"

    //  alias(libs.plugins.androidApplication) apply false
    //  alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
}

group = "com.kos"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://repository.groupdocs.com/repo/")
}


val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val targetArch = when (val osArch = System.getProperty("os.arch")) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val target = "${targetOs}-${targetArch}"


kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "BoxDrawer.js"
            }
        }
        binaries.executable()
    }

    //jvm("desktop")

    sourceSets {
      //  val desktopMain by getting
   //     val jvmTest by getting
        val jvmMain by getting

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
            implementation("com.google.code.gson:gson:2.9.1")
            implementation("com.squareup.retrofit2:retrofit:2.9.0")
            implementation("com.squareup.retrofit2:converter-gson:2.9.0")
            implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            implementation("io.github.pdvrieze.xmlutil:core:0.86.3")
            implementation("io.github.pdvrieze.xmlutil:serialization:0.86.3")
            implementation("androidx.collection:collection:1.4.0-rc01")
            implementation("androidx.collection:collection-ktx:1.4.0-rc01")
            implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:0.7.90")
            implementation(project(":figure"))
            implementation(project(":dxfprinter"))

            // implementation("com.groupdocs:groupdocs-comparison:22.3")
        }

//        desktopMain.dependencies {
//            implementation(compose.desktop.currentOs)
//        }
    }

}


compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "boxdrawer"
            packageVersion = "2.0.0"
        }
    }
}

compose.experimental {
    web.application {}
}