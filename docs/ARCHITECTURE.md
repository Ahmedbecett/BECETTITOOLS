# Architecture

## MVVM + Clean Architecture
- `ui` (Compose screens) → `ViewModel` (StateFlow) → `domain` (models/usecases) → `data` (Room/DataStore/remote/repository)
- `core` for navigation, permissions, security, utils; `design` for theme/components; `monetization` for ads abstraction.

## Data
- Room `BecettiDatabase` v1: `notes`, `favorites`, `calculation_history`, `recent_tools` (max 100 history)
- DataStore `becetti_prefs` + `settings`: language/theme and cached currency rates/date; UMP owns advertising consent state. Wi-Fi received files remain in app-private storage until the user exports/deletes them.
- Remote: `FrankfurterApi` (`api.frankfurter.app/latest?from=BASE`, ECB rates) via Retrofit

## Navigation
- `Route` sealed class + `BecettiNavGraph` (Scaffold + BottomBar + NavHost). Recent-tools tracked via `RecentToolDao`.

## Security
- `SecurityUtils.secureRandomPassword` (SecureRandom) + `passwordStrength` (length/type checks), local only.

## Ads
- `AdsManager` centralizes the Google Mobile Ads SDK, UMP consent gating, the existing Monetag/Adsterra placeholders, and `AdPolicy`. Google banner/native placements are limited to Home; UMP consent is no longer represented by a local preference toggle.

## Offline
- All tools except live currency rates work offline; currency shows cached timestamp + offline indicator.

## Permissions
- Manifest: CAMERA, INTERNET, ACCESS_NETWORK_STATE. Files use Android's SAF picker; local Wi-Fi sockets require no broad storage access. Runtime camera permission is requested only in the QR scanner.
