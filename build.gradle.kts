import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    id("org.jetbrains.compose") version "1.4.0"
}

group = "me.shreysindher"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "wordleguesser"
            packageVersion = "1.0.0"

            val iconsRoot = project.file("src/main/resources/drawables")
            linux { iconFile.set(iconsRoot.resolve("launcher_icons/linux.png")) }
            windows { iconFile.set(iconsRoot.resolve("launcher_icons/windows.ico")) }
            macOS { iconFile.set(iconsRoot.resolve("launcher_icons/macos.icns")) }
        }
    }
}