# Privacy Policy — BECETTI TOOLS (com.becetti.tools) v1.1.0

**Effective: 2026-11-10**

## Summary
BECETTI TOOLS is an offline-capable utility app. Local tools can be used without an account; online features require a network connection.

## Data and processing
- **On-device data** (Room/DataStore): notes, favorites, recent tools, calculation history, language/theme preferences, and cached currency rates/date. You can clear calculation history in Settings.
- **Passwords and text tools**: generation and analysis run locally; passwords are not uploaded by those tools.
- **Camera** (QR Scanner only): frames are processed on-device via ML Kit and are not stored by the scanner.
- **Files**: images/PDFs selected through Android's document picker are processed locally by the app; outputs are saved to the selected destination. Files received over local Wi-Fi are saved in the app's private storage until you export or delete them. Temporary cleanup removes only the app cache.
- **Currency network requests**: the currency converter requests public exchange rates from `api.frankfurter.app` over HTTPS.
- **Local Wi-Fi transfer**: when you start receiving, the app listens only on the active Wi-Fi interface. Paired devices exchange selected files over that local network using an ephemeral QR pairing key and authenticated encryption; the app does not upload these transfers to a cloud service.
- **Advertising**: Google Mobile Ads is integrated for the configured banner and native ad placements. When ad requests are made, Google Mobile Ads may process device information, identifiers, IP address, and ad interaction/diagnostic information according to Google's policies and the settings of the AdMob account. Consult Google's privacy disclosures for current details.

## Consent and choices
The Google UMP SDK refreshes consent information at app launch and presents configured messages when required. Ad requests are gated on UMP's `canRequestAds()` result. A Settings entry point for UMP privacy choices is shown when UMP indicates that one is required. The in-app privacy options do not replace a review of applicable regional disclosures or legal requirements. UMP integration alone is not a claim of GDPR or other legal compliance.

## Permissions and network
- Camera: requested for QR scanning when the scanner is opened.
- Internet/NetworkState: currency rates and ad requests.
- Cleartext traffic is disabled in the Android manifest.

## Children / sensitive information
The app does not intentionally request sensitive information for its utility features. Ad suitability, age treatment, and required disclosures must be reviewed and configured in the publisher account for the intended audience and regions.

## Changes and contact
Updates are posted here and in-app. Contact: `contact@becetti.tools` / GitHub Issues.

*This policy describes the current app implementation and is not legal advice. Review it against actual account configuration, applicable law, and distribution-store requirements before release.*
