import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    // kotlin("multiplatform")
    //  id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.21"

    //  alias(libs.plugins.androidApplication) apply false
    //  alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) // apply false
    alias(libs.plugins.compose.compiler) //apply false
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
    maven( "https://jitpack.io" )
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
        //jvmToolchain(17)
        withJava()
    }

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        moduleName = "composeApp"
//        browser {
//            commonWebpackConfig {
//                outputFileName = "BoxDrawer.js"
//            }
//        }
//        binaries.executable()
//    }

    //jvm("desktop")

    composeCompiler {
        enableStrongSkippingMode = true
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }

    sourceSets {
      //  val desktopMain by getting
   //     val jvmTest by getting
        val jvmMain by getting
  //     val figure by getting

        val lwjglVersion = "3.3.1"

        commonMain.dependencies {
            implementation(compose.components.resources)
        }

//        figure.dependencies {
//            implementation("org.locationtech.jts:jts-core:1.19.0")
//        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
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
            implementation("androidx.collection:collection:1.4.4")
            implementation("androidx.collection:collection-ktx:1.4.4")
            implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:0.8.15")
            implementation("com.darkrockstudios:mpfilepicker:3.1.0")
            implementation("org.lwjgl:lwjgl-tinyfd:${lwjglVersion}")
            implementation("org.locationtech.jts:jts-core:1.19.0")
            implementation("io.github.windedge.table:table-m3:0.1.8")
            implementation("com.github.Allbus:ArithmeticDiff:1.1.1")
            implementation( "com.google.dagger:dagger:2.51.1")
            implementation("org.apache.commons:commons-geometry-euclidean:1.0")
            implementation("org.apache.commons:commons-math3:3.6.1")
            compileOnly( "com.google.dagger:dagger-compiler:2.51.1")
            implementation(project(":figure"))
            implementation(project(":dxfprinter"))
            implementation(project(":commonMain"))
            implementation( "com.google.android.material:material:1.12.0")
            // implementation("com.groupdocs:groupdocs-comparison:22.3")
        }

        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.junit.jupiter:junit-jupiter:5.10.2")
        //    implementation(project(":src:jvmMain"))
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
            packageVersion = "2.6.0"
            windows{
                iconFile.set(project.file("robot.ico"))
                includeAllModules = true
            }
        }
    }
}

//composeCompiler {
//    enableStrongSkippingMode = true
//}

//compose.experimental {
//    web.application {}
//}