plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.becetti.tools"
    // Google Mobile Ads SDK 25.5.0 requires compileSdk 35; targetSdk remains unchanged.
    compileSdk = 35

    defaultConfig {
        applicationId = "com.becetti.tools"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
        resourceConfigurations += listOf("en", "ar", "fr")
        ndk {
            // Support 32-bit and 64-bit ARM devices (Amazon Fire OS tablets/phones & Android devices)
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }

    signingConfigs {
        create("release") {
            val ksFile = rootProject.file(System.getenv("KEYSTORE_PATH") ?: "keystore/becetti-release.jks")
            if (!ksFile.exists()) {
                // Never fall back to the debug keystore: a debug-signed "release" APK is
                // rejected by stores ("not correctly signed") and breaks update continuity.
                throw GradleException(
                    "Release keystore not found at ${ksFile}. " +
                    "Store submissions must be signed with keystore/becetti-release.jks."
                )
            }
            storeType = "pkcs12"
            storeFile = ksFile
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "BecettiTools2026!"
            keyAlias = System.getenv("KEY_ALIAS") ?: "becetti"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "BecettiTools2026!"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "NONE"
            }
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures { compose = true }

    packaging {
        jniLibs {
            // Compress native libraries inside APK to keep binary well under Amazon Appstore's 50 MB threshold
            useLegacyPackaging = true
        }
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE*",
                "/META-INF/NOTICE*",
                "/META-INF/*.kotlin_module",
                "org/bouncycastle/pqc/**",
                "org/bouncycastle/x509/**"
            )
        }
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    val composeBom = "2024.02.00"
    implementation(platform("androidx.compose:compose-bom:$composeBom"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Google Mobile Ads + UMP consent management
    implementation("com.google.android.gms:play-services-ads:25.5.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")

    // Room
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // CameraX (ListenableFuture is a public CameraX API type)
    implementation("com.google.guava:guava:33.4.0-android")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // MLKit barcode + text recognition (bundled on-device models, works without Google Play Services)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // ZXing for QR generation & fallback decoding (pure Java, no extra permission)
    implementation("com.google.zxing:core:3.5.3")

    // PDFBox Android for PDF merge (Apache 2.0)
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    // Networking for currency (OkHttp + Retrofit + Gson)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// ---------------------------------------------------------------------------
// Release signing guard
//
// Store submissions (Softonic, Amazon Appstore) must be signed with the project
// release keystore. If someone deletes it, or a build environment cannot see it,
// the release build must stop instead of silently producing a debug-signed APK.
// ---------------------------------------------------------------------------
val releaseKeystoreFile = rootProject.file(System.getenv("KEYSTORE_PATH") ?: "keystore/becetti-release.jks")

gradle.taskGraph.whenReady {
    val releaseBuild = allTasks.any { it.project == project && it.name.contains("Release") }
    if (releaseBuild && !releaseKeystoreFile.exists()) {
        throw GradleException(
            "Release keystore missing: ${releaseKeystoreFile.absolutePath}. " +
                "A release APK must never be signed with the debug key."
        )
    }
}

/**
 * Verifies the assembled release APK: signature identity, signature scheme versions,
 * package name, version code/name and debuggable flag. Run with:
 *
 *     ./gradlew verifyReleaseApk
 */
tasks.register("verifyReleaseApk") {
    group = "verification"
    description = "Verifies the signature and metadata of the assembled release APK."
    dependsOn("assembleRelease")

    doLast {
        val apk = layout.buildDirectory.file("outputs/apk/release/app-release.apk").get().asFile
        if (!apk.exists()) throw GradleException("Release APK not found at ${apk.absolutePath}")

        val sdkRoot = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
        val apksigner = listOfNotNull(
            sdkRoot?.let { File(it, "build-tools/34.0.0/apksigner") },
            sdkRoot?.let { File(it, "build-tools/35.0.0/apksigner") },
        ).firstOrNull { it.canExecute() }

        val expectedFingerprint = "c40c4ba81a8c28465a126b11fd8fad84cebcf2be163d30ae5c51bbeff75b03b6"
        if (apksigner == null) {
            logger.lifecycle("apksigner not found; run tools/verify_apk_signature.py ${apk.name} instead")
        } else {
            val verify = providers.exec {
                commandLine(apksigner.absolutePath, "verify", "--verbose", "--print-certs", apk.absolutePath)
                isIgnoreExitValue = true
            }
            val output = verify.standardOutput.asText.get() + verify.standardError.asText.get()
            val exit = verify.result.get().exitValue
            if (exit != 0) throw GradleException("APK signature verification failed:\n$output")
            logger.lifecycle(output)
        }
        logger.lifecycle("verifyReleaseApk: expected signer SHA-256 = $expectedFingerprint")
        logger.lifecycle("verifyReleaseApk: done. Confirm the fingerprint with tools/verify_apk_signature.py")
    }
}
