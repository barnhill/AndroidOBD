val gradle: String = "8.3"

plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.gradle.cachefix) apply false
    alias(libs.plugins.dokka)
}

tasks {
    wrapper {
        gradleVersion = gradle
        distributionType = Wrapper.DistributionType.BIN
    }
}