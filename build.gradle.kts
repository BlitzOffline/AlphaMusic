import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.21"
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.blitzoffline"
version = "1.0.0"

application {
    mainClass.set("com.blitzoffline.alphamusic.ApplicationKt")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
    maven("https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.21")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.slf4j:slf4j-log4j12:2.0.3")
    implementation("net.dv8tion:JDA:5.0.0-alpha.18") {
        exclude(module = "opus-java")
    }
    implementation("com.github.walkyst:lavaplayer-fork:custom-SNAPSHOT")
    implementation("com.github.BlitzOffline:Pagination-Utils:-SNAPSHOT")
    implementation("dev.triumphteam:triumph-cmd-jda-slash:2.0.0-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
        }
    }

    withType<ShadowJar> {

        relocate("kotlin", "com.blitzoffline.alphamusic.libs.kotlin")
        relocate("org.slf4j", "com.blitzoffline.alphamusic.libs.slf4j")
        relocate("dev.triumphteam.cmd", "com.blitzoffline.alphamusic.libs.commands")

        archiveFileName.set("AlphaMusic.jar")
    }
}