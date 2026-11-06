# Privacy Policy — BECETTI TOOLS (com.becetti.tools) v1.0.0

**Effective: 2026-11-05**

## Summary
BECETTI TOOLS is offline-first, no account required. We minimize data collection.

## Data We Collect
- **Local only** (on-device Room/DataStore): notes, favorites, recent tools, calculation history, language/theme, cached currency rates/date, ad consent flag. You can clear history in History/Settings.
- **No plaintext passwords stored.** Generator/strength run locally; passwords never uploaded.
- **Camera** (QR Scanner only, when you open it): frames processed on-device via ML Kit; not stored.
- **Files**: images/PDFs you select via SAF are processed locally (PdfDocument/PDFBox/ML Kit). Originals preserved; outputs saved to Documents/Pictures/Becetti.
- **Network**: currency rates from `api.frankfurter.app` over HTTPS; no personal data sent except base currency. Advertising SDKs (if enabled) may collect per their policies — disclosed below.

## Advertising
- **Monetag**: initialized only with official SDK + your publisher App ID. Test mode in dev. No ads if SDK unavailable. See Monetag privacy policy when integrated.
- **Adsterra SmartLink**: optional “Sponsored Offers” section; tapped explicitly; opens external browser; no auto-redirect; no web-script injection into native activity.
- Frequency caps, no ads on calculator, no ads over controls, consent-aware.

## Permissions
- Camera: QR scanning, requested only when opening scanner (rationale provided).
- Media/Files: image/PDF selection via SAF; processed locally.
- Internet/NetworkState: currency + ads.

## Your Controls
- Clear history, choose language/theme, grant/revoke ad consent in Settings. Uninstall clears local data. `android:allowBackup=true` (disable if policy requires).

## Children / Sensitive
- No knowing collection from children; no unnecessary identifiers; never expose secrets in APK.

## Changes & Contact
- Updates posted here + in-app. Contact: `contact@becetti.tools` / GitHub Issues.

*This is a template; review with counsel before Play publishing.*
