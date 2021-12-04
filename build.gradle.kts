import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.0"
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.0"
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
    maven("https://repo.triumphteam.dev/artifactory/public/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.slf4j:slf4j-log4j12:1.7.32")
    implementation("net.dv8tion:JDA:5.0.0-alpha1")
    implementation("com.github.walkyst:lavaplayer-fork:1.3.96")
    implementation("com.github.ygimenez:Pagination-Utils:3.0.4")
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