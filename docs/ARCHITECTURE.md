# Architecture

## MVVM + Clean Architecture
- `ui` (Compose screens) → `ViewModel` (StateFlow) → `domain` (models/usecases) → `data` (Room/DataStore/remote/repository)
- `core` for navigation, permissions, security, utils; `design` for theme/components; `monetization` for ads abstraction.

## Data
- Room `BecettiDatabase` v1: `notes`, `favorites`, `calculation_history`, `recent_tools` (max 100 history)
- DataStore `becetti_prefs` + `settings`: language/theme/consent, cached currency rates/date
- Remote: `FrankfurterApi` (`api.frankfurter.app/latest?from=BASE`, ECB rates) via Retrofit

## Navigation
- `Route` sealed class + `BecettiNavGraph` (Scaffold + BottomBar + NavHost). Recent-tools tracked via `RecentToolDao`.

## Security
- `SecurityUtils.secureRandomPassword` (SecureRandom) + `passwordStrength` (length/type checks), local only.

## Ads
- `AdsManager` centralizes `MonetagManager` (SDK gate) + `AdsterraManager` (SmartLink intent) + `ConsentManager` (DataStore) + `AdPolicy` (90s/3 per session, no-ad routes).

## Offline
- All tools except live currency rates work offline; currency shows cached timestamp + offline indicator.

## Permissions
- Manifest: CAMERA, INTERNET, ACCESS_NETWORK_STATE, READ_MEDIA_IMAGES; runtime camera only in scanner.
