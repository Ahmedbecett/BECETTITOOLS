# BECETTI TOOLS — Native & Amazon Release Verification

The app remains a native Android application built with Kotlin, Jetpack Compose, and Material 3.

## Current published baseline
- Amazon Appstore release: v1.0.0 (`versionCode = 1`). The checked-in `BECETTI-TOOLS-v1.0.0-release.apk` is unchanged and does not contain the current AdMob/Wi-Fi changes.
- Package remains `com.becetti.tools`; `minSdk 24`, `targetSdk 34`; ARM ABIs remain `armeabi-v7a` and `arm64-v8a`.

## Candidate source version
- Version configured in Gradle remains v1.0.0 (`versionCode = 1`) until a candidate build is verified; v1.1.0 (`versionCode = 2`) is only the planned next version.
- Candidate APK has not been built or device-tested in this workspace. Do not submit it to Amazon until a successful signed Release build, APK size/signature checks, Fire OS testing, and two-device Wi-Fi transfer tests are complete.
- Google Mobile Ads availability on Fire OS devices without Google Play services must be tested; ad failure must not block core tools.
- Received APKs are stored as files; the app does not install them automatically.

## Build and verify
```bash
./gradlew assembleRelease
```
Then verify APK signature schemes, `debuggable=false`, package/version, <50 MB store limit, and operation on representative Android and Fire OS devices. The current sandbox could not download Gradle 8.7, so these checks remain pending.
