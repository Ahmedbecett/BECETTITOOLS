# BECETTI TOOLS — Native Verification

This project is **100% Native Android** — Kotlin + Jetpack Compose + Material3.

## No WebView
- No `WebView` import in `app/src` (verified by CI: `! grep -r WebView app/src`)
- No `android.webkit` usage
- All tools are native: Room, DataStore, CameraX, MLKit, ZXing, PDFBox, Retrofit

## Tech Proof
- `MainActivity: ComponentActivity` + `setContent { BecettiToolsTheme { BecettiNavGraph } }`
- `build.gradle.kts`: `androidx.compose:compose-bom`, `activity-compose`, `navigation-compose`, `room`, `datastore`, `camerax`
- `BecettiToolsApp: Application` with Room + AdsManager

## Build Locally
```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

## CI Builds Native APK automatically
Workflow `.github/workflows/build-native-apk.yml` runs on every push to `arena/354c3e44-becettitools` and uploads artifact `BECETTI-TOOLS-v1.0.0-debug-native`.
