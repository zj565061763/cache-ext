plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

val libGroupId = "com.sd.lib.android"
val libArtifactId = "cache-ext"
val libVersionName = "1.0.0-alpha14"

android {
    namespace = "com.sd.lib.cache.ext"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }

    kotlinOptions {
        freeCompilerArgs += "-module-name=$libGroupId.$libArtifactId"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    api(libs.sd.cache)
    api(libs.kotlin.coroutines)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = libGroupId
            artifactId = libArtifactId
            version = libVersionName

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}