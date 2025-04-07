plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.toml.version.checker)
}

version = "1.7.0"
group = "com.pnuema.android"

android {
    base.archivesName.set("obd")
    namespace = "com.pnuema.android.obd"
    compileSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    kotlin {
        jvmToolchain(JavaVersion.VERSION_17.toString().toInt())
    }
}

dependencies {
    implementation(libs.evalex)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.startup)
}

val dokkaOutputDir = layout.buildDirectory.dir("dokka")
tasks {
    val sourcesJar by registering(Jar::class, fun Jar.() {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    })

    val javadocJar by registering(Jar::class, fun Jar.() {
        dependsOn.add(dokkaGenerate)
        archiveClassifier.set("javadoc")
        from(android.sourceSets.getByName("main").java.srcDirs)
        from(dokkaOutputDir)
    })

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
    }

    dokka {
        moduleName.set("SaveStateObserver")
        dokkaPublications.html {
            suppressInheritedMembers.set(true)
            failOnWarning.set(true)
            outputDirectory.set(dokkaOutputDir)
        }
        dokkaSourceSets.main {
            sourceLink {
                localDirectory.set(file("src/main/java"))
                remoteUrl("https://github.com/barnhill/AndroidOBD")
            }
        }
        pluginsConfiguration.html {
            footerMessage.set("(c) Brad Barnhill")
        }
    }

    build {
        dependsOn(dokkaGenerate)
    }
}
