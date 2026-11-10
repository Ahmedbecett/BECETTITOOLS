plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
        versionCode = 1
        versionName = "1.0.0"
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
            if (ksFile.exists()) {
                storeFile = ksFile
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "BecettiTools2026!"
                keyAlias = System.getenv("KEY_ALIAS") ?: "becetti"
                keyPassword = System.getenv("KEY_PASSWORD") ?: "BecettiTools2026!"
            } else {
                val debugKs = signingConfigs.getByName("debug")
                storeFile = debugKs.storeFile
                storePassword = debugKs.storePassword
                keyAlias = debugKs.keyAlias
                keyPassword = debugKs.keyPassword
            }
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

    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }

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
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // CameraX
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
