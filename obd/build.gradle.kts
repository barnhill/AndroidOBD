buildscript {
    extra["javaVersion"] = JavaVersion.VERSION_17
}

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.toml.version.checker)
}

version = "1.4.1"
group = "com.pnuema.android"
val javaVersion: JavaVersion by extra

android {
    base.archivesName.set("obd")
    namespace = "com.pnuema.android.obd"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }

    kotlin {
        jvmToolchain(javaVersion.toString().toInt())
    }
}

dependencies {
    implementation(libs.evalex)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.startup)
}

val dokkaOutputDir = "$buildDir/docs"
tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaOutputDir)
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
    }

    dokkaHtml {
        outputDirectory.set(file(dokkaOutputDir))
        dokkaSourceSets {
            named("main") {
                noAndroidSdkLink.set(false)
            }
        }
    }
}
