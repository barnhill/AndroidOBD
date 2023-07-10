plugins {
    alias(libs.plugins.android).apply(true)
    alias(libs.plugins.kotlin.android).apply(true)
    alias(libs.plugins.maven.publish).apply(true)
    alias(libs.plugins.kotlin.serialization).apply(true)
    alias(libs.plugins.dokka).apply(true)
    alias(libs.plugins.toml.version.checker).apply(true)
}

version = "1.4.1"
group = "com.pnuema.android"

ext {
    val baseUrl = "https://github.com/barnhill"
    val siteUrl = "https://github.com/barnhill/AndroidOBD"
    val gitUrl = "https://github.com/barnhill/AndroidOBD.git"
    val descr = "Android library to communicate with ELM327 based OBD devices"
}

android {
    namespace = "com.pnuema.android.obd"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = version as String
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation (libs.evalex)
    implementation (libs.kotlinx.serialization.json)
    implementation (libs.androidx.startup)
}

val dokkaOutputDir = "$buildDir/dokka"
tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(android.sourceSets.getByName("main").java.srcDirs)
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
