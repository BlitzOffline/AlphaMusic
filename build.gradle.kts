import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.blitzoffline"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
    maven("https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.slf4j:slf4j-log4j12:2.0.7")
    implementation("net.dv8tion:JDA:5.0.0-beta.9") {
        exclude(module = "opus-java")
    }
    implementation("com.github.Walkyst.lavaplayer-fork:lavaplayer:ef075855da")
    implementation("com.github.ygimenez:Pagination-Utils:4.0.3")
    implementation("dev.triumphteam:triumph-cmd-jda-slash:2.0.0-ALPHA-8")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks {
    withType<Jar> {
        manifest {
            attributes["Specification-Version"] = project.version
            attributes["Implementation-Version"] = project.version
            attributes["Main-Class"] = "com.blitzoffline.alphamusic.ApplicationKt"
        }
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
        }
    }

    withType<ShadowJar> {

        relocate("kotlin", "com.blitzoffline.alphamusic.libs.kotlin")
        relocate("dev.triumphteam.cmd", "com.blitzoffline.alphamusic.libs.commands")

        archiveFileName.set("AlphaMusic.jar")
    }
}