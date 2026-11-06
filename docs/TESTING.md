# Testing Report — 1.0.0

## Pre-reqs
- Android Studio Hedgehog+, `assembleDebug` builds; test on physical Android 13 device + emulator low-end profile.

## Checklist (spec §12)
1. **Build APK**: `./gradlew assembleDebug` → verify `app-debug.apk` installs.
2. **15 tools functional**: run each tool's happy + error path (see below).
3. **AR RTL**: set language AR, verify RTL mirroring, strings `values-ar`.
4. **FR**: set FR, verify strings.
5. **EN**: default.
6. **Android 13**: targetSdk 34, `READ_MEDIA_IMAGES` (not `READ_EXTERNAL_STORAGE`), SAF flows.
7. **Offline**: airplane mode — all except live currency must work; currency shows offline indicator + cached timestamp.
8. **PDF exports**: Image→PDF (multi-select, A4/A3/Letter) + Merger → check `Documents/Becetti` outputs, originals preserved.
9. **QR scanning**: grant camera only on scanner screen, scan QR, confirm link-then-open.
10. **File permissions**: SAF pickers, deny then retry; no crash.
11. **Restarts**: kill + relaunch, DataStore persists language/theme, Room persists history/favorites/notes.
12. **Ad failure**: disable Monetag SDK (default) + no SmartLink URL + offline → no crash, `canShowAd` false on calculator, cap 90s/3/session.
13. **Low-memory**: trigger “Don’t keep activities”, background/foreground, no NPE.
14. **Battery**: no wake locks; camera unbinds on dispose; lightweight recompositions.
15. **Privacy**: rationale strings, Settings disclosures, `PRIVACY_POLICY.md`, no secret in APK (`grep -R API_KEY`).

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
- All checks PASS on emulator Pixel 7 Android 13 (2026-11-05). Attach screenshots + `adb logcat` if publishing.
