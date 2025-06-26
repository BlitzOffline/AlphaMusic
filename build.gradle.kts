import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
}

group = "com.blitzoffline"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
    maven("https://repo.triumphteam.dev/snapshots/")
    maven("https://maven.lavalink.dev/releases")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.commons.cli)
    implementation(libs.slf4j.log4j12)
    implementation(libs.jda) {
        exclude(module = "opus-java")
    }
    implementation(libs.lavalink.youtube.common)
    implementation(libs.lavalink.youtube.v2)
    implementation(libs.lavalink.lavaplayer)
    implementation(libs.pagination.utils)
    implementation(libs.triumph.cmd.jda.slash)
    implementation(libs.mysql.connector)
    implementation(libs.hikaricp)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
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
        archiveFileName.set("AlphaMusic.jar")
    }
}