import org.jetbrains.kotlin.gradle.plugin.extraProperties

val gradle: String = "8.2.1"

plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.gradle.cachefix) apply false
    alias(libs.plugins.dokka) apply false
}

tasks {
    wrapper {
        gradleVersion = gradle
        distributionType = Wrapper.DistributionType.BIN
    }
}