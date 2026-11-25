# Softonic resubmission — BECETTI TOOLS v1.1.0

Two rejection reasons, two fixes. Both are verifiable by anyone with the repository.

| # | Softonic rejection | Fix in this branch | Proof |
|---|---|---|---|
| 1 | "the submitted APK file is either not correctly signed or the signature is invalid/corrupted upon verification" | release build can no longer fall back to a debug key; keystore identity is asserted before every build; signature is verified by two independent tools | `tools/verify_apk_signature.py`, CI `apksigner verify` + certificate continuity with v1.0.0 |
| 2 | "Arabic and French are declared, but title/caption/description are in English" | Arabic and French title, caption and description; complete AR/FR string resources; English wording removed | `docs/store/softonic-v1.1.0-metadata.json`, `tools/check_store_metadata.py`, `tools/check_localization.py` |

## 1. APK to upload

| Property | Value |
|---|---|
| File | `BECETTI-TOOLS-v1.1.0-softonic.apk` |
| Size | **19,753,923 bytes** (18.84 MiB) |
| SHA-256 | `3767f6f9ef8763f0ab1db827bf52077444c793aa470b4ab27ecfb8df979cfe9b` |
| Package | `com.becetti.tools` (unchanged) |
| Version | `versionName 1.1.0` / `versionCode 2` |
| minSdk / targetSdk | 24 (Android 7.0) / 34 |
| Signing schemes | APK Signature Scheme **v1 + v2 + v3** |
| Signer certificate | `CN=Becetti Tools, O=Becetti, L=Chlef, ST=Chlef, C=DZ` |
| Signer certificate SHA-256 | `c40c4ba81a8c28465a126b11fd8fad84cebcf2be163d30ae5c51bbeff75b03b6` (identical to v1.0.0 → updates keep working) |
| Debuggable | no |

The equivalent pristine CI build `BECETTI-TOOLS-v1.1.0-release.apk`
(SHA-256 `e8ca93a74acdfefaab172d233264db25a1b816764ca17a11b2174a33148ff786`, 19,767,107 bytes)
is also valid.

### What the `-softonic.apk` variant is

AGP writes two optional ID-value pairs into the APK Signing Block next to the real signature
blocks: `0x504b4453` (dependency-info) and `0x42726577` (verity padding). They are irrelevant to
APK verification, but some strict third-party checkers reject a signing block they do not
expect. `tools/make_softonic_apk.py` rebuilds the signing block with **only** the v2
(`0x7109871a`) and v3 (`0xf05368c0`) blocks, adjusts the size fields and the EOCD central
directory offset, keeps every signature byte-identical, and refuses to write the file unless
`tools/verify_apk_signature.py` passes on the result.

```
APK Signing Block, release APK:  v2, v3, dependency-info, verity padding
APK Signing Block, softonic APK: v2, v3
```

Both files install and update identically; the difference is 13,184 bytes of signing-block
padding only.

## 2. Check the file before uploading

A correctly signed APK fails a store check when the uploaded bytes differ from the built
bytes: a truncated download, a re-zipped archive or a chat-app re-compression all break the
signature, because the signatures cover the whole file.

```bash
sha256sum BECETTI-TOOLS-v1.1.0-softonic.apk
# 3767f6f9ef8763f0ab1db827bf52077444c793aa470b4ab27ecfb8df979cfe9b
```

Windows: `certutil -hashfile BECETTI-TOOLS-v1.1.0-softonic.apk SHA256`, and the file size must
be exactly `19,753,923` bytes. Upload the file directly — do not re-zip, rename the extension,
or send it through apps that compress attachments.

## 3. Verify the signature yourself

No Android tooling is required:

```bash
pip install cryptography asn1crypto
python3 tools/verify_apk_signature.py BECETTI-TOOLS-v1.1.0-softonic.apk
```

Expected result:

```
PASS  v1 (JAR) (646 entries)
PASS  APK Signature Scheme v2 (1 signer(s))
PASS  APK Signature Scheme v3 (1 signer(s))
PASS  single signing identity (1 certificate)

OVERALL: VALID — APK signature verified
```

With Android build-tools installed:

```bash
apksigner verify --verbose --print-certs BECETTI-TOOLS-v1.1.0-softonic.apk
```

## 4. Paste these into the Softonic form

The full Arabic and French texts live in `docs/store/softonic-v1.1.0-metadata.json`
(machine-checked) and are also readable in `docs/STORE_METADATA_AR_FR.md`.

| Field | Arabic (`ar`) | French (`fr`) |
|---|---|---|
| Title | `أدوات بيسيتي` | `BECETTI OUTILS` |
| Caption / short description | `20 أداة ذكية في تطبيق واحد: حاسبة، عملات، QR، PDF، صور وملاحظات — بدون إنترنت.` | `20 outils intelligents : calcul, devises, QR, PDF, images et notes — hors-ligne.` |
| Description | see JSON — 2,745 characters | see JSON — 3,642 characters |

Limits enforced by `tools/check_store_metadata.py`: title ≤ 50, caption ≤ 80,
description ≤ 4000 characters.

Leftover English text is the usual cause of this rejection: replace every remaining English
title, caption or description in the submission form with the texts above, and keep the
supported-language selection on **Arabic + French**.

## 5. Signature fixes made in the build itself

* `app/build.gradle.kts` throws `GradleException` when `keystore/becetti-release.jks` is
  missing — the previous config silently signed release APKs with the debug key in that case.
* A `gradle.taskGraph.whenReady` guard now blocks any `*Release*` task when the keystore is
  absent, so a debug-signed "release" APK cannot be produced even accidentally.
* The keystore is declared as `pkcs12` and holds the original certificate, so existing
  installs from v1.0.0 update in place.
* New `verifyReleaseApk` Gradle task runs `apksigner verify --verbose --print-certs` after
  assembling and fails the build if verification fails.
* CI asserts the keystore fingerprint
  (`C40C4BA81A8C28465A126B11FD8FAD84CEBCF2BE163D30AE5C51BBEFF75B03B6`) before building,
  then verifies the APK with `apksigner` **and** `tools/verify_apk_signature.py`, and compares
  the signer certificate with the shipped v1.0.0 APK.

## 6. Arabic/French fixes made in the app

* `res/values-ar/strings.xml` and `res/values-fr/strings.xml` now carry the complete string
  set (app name, tagline, search hint, sponsored-offers label, camera and file permission
  rationales) instead of a partial set.
* Arabic launcher label is `أدوات بيسيتي`; French launcher label is `BECETTI OUTILS` — matching
  the store titles exactly.
* The in-app tool counter said "19 tools" while the catalog ships 20 (the Wi-Fi file transfer
  screen was added in v1.1.0). Corrected to 20 in Arabic, French and English.
* `tools/check_localization.py` fails the build if a string is missing from either locale, if
  an Arabic string contains no Arabic characters, or if a French string contains Arabic text.

## 7. Evidence table

| # | Check | Tool | Result |
|---|---|---|---|
| 1 | ZIP container integrity | `verify_apk_signature.py` | PASS (649 entries) |
| 2 | v1 (JAR): 646 entry digests, all entries covered | `verify_apk_signature.py` | PASS |
| 3 | v1: CERT.SF manifest digest + per-section digests | `verify_apk_signature.py` | PASS |
| 4 | v1: CERT.RSA PKCS#7 signature over CERT.SF | `verify_apk_signature.py` | PASS |
| 5 | v2: chunked content digest over the whole APK | `verify_apk_signature.py` | PASS |
| 6 | v2: RSA PKCS#1 v1.5 / SHA-256 signature | `verify_apk_signature.py` | PASS |
| 7 | v3: content digest + signature + SDK range 24..∞ | `verify_apk_signature.py` | PASS |
| 8 | v1 / v2 / v3 signed by the same certificate | `verify_apk_signature.py` | PASS |
| 9 | Signer certificate = committed release keystore certificate | `openssl pkcs12` + verifier | PASS |
| 10 | Signer certificate = v1.0.0 certificate (update continuity) | verifier + CI | PASS |
| 11 | Package/version `com.becetti.tools` 1.1.0 (2) | androguard + CI `aapt` | PASS |
| 12 | Not debuggable | CI `aapt dump badging` | PASS |
| 13 | Tampered variants rejected (truncated / bit-flip / appended) | `verify_apk_signature.py` | PASS (all rejected) |
| 14 | Arabic listing: 100% Arabic script, ≤ limits | `check_store_metadata.py` | PASS |
| 15 | French listing: Latin script with French accents, ≤ limits | `check_store_metadata.py` | PASS |
| 16 | AR/FR resources complete and translated | `check_localization.py` | PASS |

## 8. If Softonic rejects the verified file again

Reply with:

> The APK is signed with APK Signature Scheme v1 + v2 + v3 and passes both `apksigner verify
> --verbose` and an independent from-scratch verifier. The uploaded file is byte-exact: size
> 19,753,923 bytes, SHA-256 3767f6f9ef8763f0ab1db827bf52077444c793aa470b4ab27ecfb8df979cfe9b,
> signer certificate SHA-256 c40c4ba81a8c28465a126b11fd8fad84cebcf2be163d30ae5c51bbeff75b03b6
> (unchanged from v1.0.0). Please confirm the SHA-256 of the file you received and re-run the
> signature check.
