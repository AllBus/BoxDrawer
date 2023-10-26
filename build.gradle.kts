import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.kos"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://repository.groupdocs.com/repo/")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
                implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
                implementation ("com.google.code.gson:gson:2.9.1")
                implementation ("com.squareup.retrofit2:retrofit:2.9.0")
                implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
                implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
               // implementation("com.groupdocs:groupdocs-comparison:22.3")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "boxdrawer"
            packageVersion = "1.0.0"
        }
    }
}
