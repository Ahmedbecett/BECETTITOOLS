plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.becetti.tools"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.becetti.tools"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
        // أجهزة Amazon Fire OS تعمل بمعمليات ARM فقط (armeabi-v7a + arm64-v8a).
        // عند تمرير specialية -Pndk.abifilters=armeabi-v7a,arm64-v8a تُزال المكتبات الأصلية
        // غير الضرورية من الـ APK universale. الـ AAB يُبنى بكل البنائية ليتولى
        // متجر أمازون التوزيع حسب كل جهاز (ينصح به للنشر).
        if (project.hasProperty("ndk.abifilters")) {
            ndk {
                abiFilters.addAll(
                    project.property("ndk.abifilters").toString().split(",").map { it.trim() }
                )
            }
        }
    }
    signingConfigs {
        create("release") {
            // كلمات المرور ومسار المخزن تُSupply عبر متغيرات البيئة أو ملف keystore.properties
            // (ملف مستبعد من git لأسباب أمنية). مثال:
            //   export BECETTI_STORE_PASSWORD=... BECETTI_KEY_PASSWORD=...
            val props = java.util.Properties()
            val propFile = rootProject.file("keystore.properties")
            if (propFile.exists()) {
                props.load(java.io.FileInputStream(propFile))
            }
            val get = { name: String, prop: String, default: String = "" ->
                System.getenv(name) ?: props[prop] ?: default
            }

            storeFile = file(get("BECETTI_STORE_FILE", "storeFile", "release-keystore.jks"))
            storePassword = get("BECETTI_STORE_PASSWORD", "storePassword")
            keyAlias = get("BECETTI_KEY_ALIAS", "keyAlias", "becetti")
            keyPassword = get("BECETTI_KEY_PASSWORD", "keyPassword")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            manifestPlaceholders = mapOf("allowBackup" to "false")
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            manifestPlaceholders = mapOf("allowBackup" to "true")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/maven/{*}"
            excludes += "/about.html"
        }
    }
}

dependencies {
    val composeBom = "2024.02.00"
    implementation(platform("androidx.compose:compose-bom:$composeBom"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.2")
    // الأيقونات المستخدمة من Icons.Filled كلُّها في core — extended غير ضرورية وتزيد الحجم
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Room (ملاحظات، مفضلات، سجل)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // كو routines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // CameraX (ماسح QR فقط — يُطلب إذن الكاميرا عند الحاجة)
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // ML Kit مربوط بحزمة التطبيق (يعمل على Fire OS بدون خدمات جوجل بلاي)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // ZXing core فقط لتوليد QR — zxing-android-embedded غير مستخدم في الكود وحُذف
    implementation("com.google.zxing:core:3.5.3")

    // PDFBox Android لدمج ملفات PDF
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    // شبكة لتحويل العملات
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}
