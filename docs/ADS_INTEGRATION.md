# Ads Integration — Monetag + Adsterra

## Abstraction
- `monetization/AdsManager.kt` — centralize: `MonetagManager`, `AdsterraManager`, `ConsentManager`, `AdPolicy`
- Providers independently enable/disable; no ads if SDK unavailable.

## Monetag (official SDK)
1. Obtain SDK + App ID from Monetag publisher account.
2. Add dependency per Monetag docs (e.g., `implementation("com.monetag:android-sdk:…")`).
3. In `MonetagManager.initialize(appId)`: replace placeholder with `MonetagSdk.init(ctx, appId)`.
4. Set `isAvailable() = true` when SDK present; implement `showInterstitial()` per docs (check ad availability, error handling, test mode).
5. Init in `BecettiToolsApp.onCreate()` via `AdsManager.initialize()`.

## Adsterra SmartLink
- Set `AdsterraManager.smartLinkUrl` from remote config / publisher account.
- UI: use `AdPlaceholder` (labeled “Sponsored Offers — Tap to explore”) in `HomeScreen` etc.; on tap call `adsterra.openSponsoredOffers()` → `ACTION_VIEW` intent.
- Never auto-launch; never inject web ad scripts into native activities.

## Policy Rules (AdPolicy.kt)
- `NO_AD_ROUTES = calc_scientific`; min interval 90s; max 3/session; no forced pre-tool interstitial; no overlay on controls; respect provider policies.

## Consent
- `ConsentManager` (DataStore `ads_consent_granted`); gate personalized ads on consent; provide opt-out in Settings.

## Testing
- Test ad failure (no SDK / no network / no fill) — app must remain functional; verify frequency caps and no-ad routes.
