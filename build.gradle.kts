import org.gradle.internal.extensions.core.extra

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
        gradleVersion = libs.versions.gradle.get()
        distributionType = Wrapper.DistributionType.BIN
    }
}

rootProject.extra.set("gitVersionName", fetchGitVersionName())
rootProject.extra.set("gitVersionCode", fetchGitVersionCode())

fun fetchGitVersionCode(): String = "git rev-list HEAD --count".execute()

fun fetchGitVersionName(): String = "git describe HEAD".execute()

fun String.execute(): String {
    val process = ProcessBuilder(*split(" ").toTypedArray())
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
    process.waitFor()
    return process.inputStream.bufferedReader().readText().trim()
}