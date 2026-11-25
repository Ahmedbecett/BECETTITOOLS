# Google AdMob Integration

## SDK and identifiers
- Google Mobile Ads Android SDK: `com.google.android.gms:play-services-ads:25.5.0`.
- Google User Messaging Platform SDK: `com.google.android.ump:user-messaging-platform:4.0.0`.
- AdMob App ID: `ca-app-pub-6858364141205978~1142706527` (Android manifest metadata).
- Adaptive banner unit: `ca-app-pub-6858364141205978/8031316998` (Home screen list footer).
- Native Advanced unit: `ca-app-pub-6858364141205978/5268994651` (one distinctly labeled ad card on Home; only shown after a successful load).

## Consent and lifecycle
- UMP updates consent information at app launch, presents forms when required, gates SDK initialization/ad requests with `canRequestAds()`, and exposes the privacy options entry point in Settings only when UMP reports it is required.
- Banner size adapts to available list width; it pauses/resumes with the Activity lifecycle and is destroyed when removed. Native ad objects are destroyed when the Home ad leaves composition.
- Ad load failures are non-blocking; no empty native placeholder is displayed. Ads are not overlaid on controls and no Google ads are requested from tool screens.

## Required account configuration and verification
- Create the applicable consent messages in AdMob Privacy & messaging. Until configured, UMP may not show a consent form.
- Verify the publisher ID in the AdMob account before publishing an `app-ads.txt`; an AdMob App ID or ad unit ID is not a substitute. The repository's GitHub Pages site is currently not configured, and no unverified `app-ads.txt` is included.
- Use Google-provided test ad methods/device configuration during development. Do not click live production ads for testing; production unit IDs are retained in the release configuration.
- Check Fire OS devices without Google Play services. Ad requests are optional and failures must not block app functionality.

This SDK integration does not, by itself, certify GDPR, regional privacy-law, store-policy, or AdMob account compliance. Review account messages, disclosures, and applicable policies before release.
