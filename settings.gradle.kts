@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.13.4"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

val cacheUrl: String? = if (System.getenv("REMOTE_CACHE_URL") == null) extra["REMOTE_CACHE_URL"] as String else System.getenv("REMOTE_CACHE_URL")

if (cacheUrl != null) {
    buildCache {
        local {
            removeUnusedEntriesAfterDays = 30
        }

        remote<HttpBuildCache> {
            url = uri(cacheUrl)
            isEnabled = true
            isPush = true
            isAllowUntrustedServer = true
            isAllowInsecureProtocol = false
            if (isEnabled) {
                println("Using remote build cache: " + cacheUrl)
            }
            credentials {
                username = if (System.getenv("REMOTE_CACHE_USER") == null) extra["REMOTE_CACHE_USER"] as String else System.getenv("REMOTE_CACHE_USER")
                password = if (System.getenv("REMOTE_CACHE_PASS") == null) extra["REMOTE_CACHE_PASS"] as String else System.getenv("REMOTE_CACHE_PASS")
            }
        }
    }
} else {
    println("Not using remote build cache!")
}

rootProject.name = "obd"

include("obd")