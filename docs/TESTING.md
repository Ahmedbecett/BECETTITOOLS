# Testing Checklist — 1.1.0 (Pending execution)

## Pre-reqs
- Android Studio Hedgehog+, `assembleDebug` builds; test on physical Android 13 device + emulator low-end profile.

## Checklist (spec §12)
1. **Build APK**: `./gradlew assembleDebug` → verify `app-debug.apk` installs.
2. **19 tools**: exercise each tool's happy and error paths, including same-Wi-Fi file transfer, export, and cache cleanup.
3. **AR RTL**: set language AR, verify RTL mirroring, strings `values-ar`.
4. **FR**: set FR, verify strings.
5. **EN**: default.
6. **Android 13**: targetSdk 34, `READ_MEDIA_IMAGES` (not `READ_EXTERNAL_STORAGE`), SAF flows.
7. **Offline**: airplane mode — all except live currency must work; currency shows offline indicator + cached timestamp.
8. **PDF exports**: Image→PDF (multi-select, A4/A3/Letter) + Merger → check `Documents/Becetti` outputs, originals preserved.
9. **QR scanning**: grant camera only on scanner screen, scan QR, confirm link-then-open.
10. **File permissions**: SAF pickers, deny then retry; no crash.
11. **Restarts**: kill + relaunch, DataStore persists language/theme, Room persists history/favorites/notes.
12. **Ads/UMP**: validate consent-form flow and Settings entry point using AdMob test configuration; simulate no fill/offline and confirm tool screens remain usable.
13. **Low-memory**: trigger “Don’t keep activities”, background/foreground, no NPE.
14. **Battery**: no wake locks; camera unbinds on dispose; lightweight recompositions.
15. **Privacy**: rationale strings, Settings disclosures, `PRIVACY_POLICY.md`, no new secret in APK.
16. **Wi-Fi transfer**: two devices on the same Wi-Fi; scan pairing QR, send image/document/APK, export and delete the received copy, stop the receiver, test wrong code/disconnect and a file larger than the 512 MiB cap. Verify other app files are not accessed and temporary cleanup does not delete received files.

## Tool Smoke Tests
- Calc: `2+2*3=8`, `sqrt(9)=3`, `sin(30)=0.5`, history persists/clears.
- Unit: 1 km → 1000 m etc.
- Currency: online fetch, offline cached indicator.
- Percentage: 20% of 150=30.
- Age: 2000-01-01 → today.
- QR Gen: generate + export PNG; WiFi/Contact formats.
- Pass Gen/Strength: generate/copy, score 0–5.
- Text: upper/lower/dedup/reverse/empty-lines, counts.
- Notes: CRUD + search.
- History/Favorites: mark, persist, clear.

## Result
- Not run for this update. The sandbox could not download Gradle 8.7 from `services.gradle.org`; the APK and device-level Wi-Fi transfer remain unverified. Run this checklist on two physical devices before creating or submitting a release.
