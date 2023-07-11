buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.maven.publish).apply(false)
    alias(libs.plugins.gradle.cachefix).apply(false)
}

tasks {
    wrapper {
        gradleVersion = "8.2.1"
        distributionType = Wrapper.DistributionType.BIN
    }
}