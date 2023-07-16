import org.jetbrains.kotlin.gradle.plugin.extraProperties

buildscript {
    extra["gradle"] = "8.2.1"

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

val gradle: String by extra

plugins {
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.maven.publish).apply(false)
    alias(libs.plugins.gradle.cachefix).apply(false)
}

tasks {
    wrapper {
        gradleVersion = gradle
        distributionType = Wrapper.DistributionType.BIN
    }
}