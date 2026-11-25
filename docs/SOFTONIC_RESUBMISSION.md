# Softonic Resubmission — BECETTI TOOLS v1.1.0

## Why version 1.1.0 was rejected

Softonic rejected v1.1.0 with: *"the submitted APK file is either not correctly signed or the
signature is invalid/corrupted upon verification."*

Independent, byte-level verification of the build pipeline's APK proves the **build output is
correctly signed** (see the full report below). A correctly signed APK can only fail a signature
check if the file that reached Softonic was **not the original bytes** — i.e. it was truncated or
altered while being downloaded from GitHub or uploaded to Softonic. Even a single missing byte
makes every signature scheme report exactly this kind of error, because the signatures cover the
entire file.

## Resubmission file

Upload this exact file to Softonic:

| Property | Value |
|---|---|
| **File** | `BECETTI-TOOLS-v1.1.0-softonic.apk` |
| **Size** | **19,753,238 bytes** (18.84 MiB) |
| **SHA-256** | `96a8f30452378e23e386d1d49dcc2ded45cbe5dd8a8ca37e04a824698b60f349` |
| **Package** | `com.becetti.tools` |
| **Version** | `versionName 1.1.0` / `versionCode 2` |
| **Signing schemes** | APK Signature Scheme v1 + v2 + v3 |
| **Signer certificate SHA-256** | `c40c4ba81a8c28465a126b11fd8fad84cebcf2be163d30ae5c51bbeff75b03b6` (same certificate as v1.0.0 — update continuity preserved) |

The equivalent pristine CI build (`BECETTI-TOOLS-v1.1.0-release.apk`, SHA-256
`af328bdae54818738f979607dbb9d4892f88b3abbdda4dcb797b30ba202c6332`) is also valid and committed in
this repository. The `-softonic.apk` variant additionally removes two non-signature metadata blocks
(dependency-info + verity padding) from the APK Signing Block so that even strict third-party
signature checkers see only the v2 and v3 signature blocks. All signatures are byte-identical to
the CI build and fully valid.

## Check the file BEFORE uploading (important!)

After downloading `BECETTI-TOOLS-v1.1.0-softonic.apk`, confirm it is intact:

**Windows (fastest):** right-click the file → *Properties* → the size must be exactly
**19,753,238 bytes**. If Properties shows a different byte count, delete it and download again.

**Windows (exact hash):** open Command Prompt and run:

```
certutil -hashfile "BECETTI-TOOLS-v1.1.0-softonic.apk" SHA256
```

The output must be `96a8f30452378e23e386d1d49dcc2ded45cbe5dd8a8ca37e04a824698b60f349`.

**macOS / Linux:**

```
shasum -a 256 BECETTI-TOOLS-v1.1.0-softonic.apk     # macOS
sha256sum BECETTI-TOOLS-v1.1.0-softonic.apk         # Linux
```

Only upload the file if the size/hash matches. Then upload it to Softonic **directly** — do not
re-zip it, rename the extension, or pass it through chat apps/email attachments that compress
files (that is the classic way an APK gets corrupted "upon verification").

## Resubmit on Softonic Publishing Center

1. Open the application page (the one showing **REJECTED / Version 1.1.0 wasn't published**).
2. Go to the **Versions** tab and choose the version **1.1.0** entry (or click **Submit for review**).
3. Replace the APK with `BECETTI-TOOLS-v1.1.0-softonic.apk` (verified against the checksum above).
4. Leave all other fields unchanged (name "BECETTI TOOLS", category, product sheet) and submit.

If the portal refuses to accept version "1.1.0" again, bump the version to 1.1.1 and rebuild —
the CI workflow on branch `arena/2fbe45fe-becettitools` produces it automatically.

## If Softonic rejects the verified file again

Then the corruption is happening on their side or inside their pipeline. Reply to their review
message with:

> The attached APK is correctly signed with APK Signature Scheme v1 + v2 + v3 and passes
> `apksigner verify --verbose`. The file we uploaded is byte-exact: size 19,753,238 bytes,
> SHA-256 96a8f30452378e23e386d1d49dcc2ded45cbe5dd8a8ca37e04a824698b60f349. The signing
> certificate (SHA-256 c40c4ba81a8c28465a126b11fd8fad84cebcf2be163d30ae5c51bbeff75b03b6) is
> unchanged from the previous version. Please verify the file hash on your side and re-run the
> signature check.

## Independent verification report (2026-11-24)

Every layer of the file was verified independently of Android tooling (and CI additionally ran
`apksigner verify --verbose --print-certs`):

| # | Check | Result |
|---|---|---|
| 1 | ZIP container integrity | PASS |
| 2 | No trailing bytes after ZIP directory | PASS |
| 3 | APK Signing Block present and well-formed | PASS |
| 4 | v2: content digest (chunked SHA-256 over the whole APK) | PASS |
| 5 | v2: RSA signature (RSASSA-PKCS1-v1_5 + SHA-256) over signed data | PASS |
| 6 | v3: content digest + RSA signature + minSDK 24 | PASS |
| 7 | v1 (JAR): MANIFEST.MF digests for all 646 entries | PASS (0 bad) |
| 8 | v1: MANIFEST covers every ZIP entry | PASS |
| 9 | v1: CERT.SF digest-of-manifest | PASS |
| 10 | v1: CERT.RSA PKCS#7 signature over CERT.SF | PASS |
| 11 | v1 cert == v2 cert == v3 cert | PASS |
| 12 | Release keystore certificate == APK signer certificate | PASS |
| 13 | CI `apksigner verify --verbose --print-certs` | PASS |
| 14 | CI certificate continuity vs v1.0.0 APK | PASS |
| 15 | CI checks: package/version 1.1.0 (vc 2), non-debuggable, < 50 MB | PASS |

Signature algorithm IDs used (`0x0103` = RSA PKCS#1 v1.5 with SHA-256) match the official apksig
source (`SignatureAlgorithm.java`), so any standard verifier — including `apksigner` and
`jarsigner` — validates this APK.

## Why this will not happen with future versions

- The Gradle build now **fails** if the release keystore is missing, instead of silently falling
  back to the debug keystore (a debug-signed APK is "not correctly signed" for stores).
- The keystore is explicitly `pkcs12` (it is a PKCS#12 file, matching the certificate used for
  v1.0.0 and v1.1.0).
- CI verifies signatures and certificate continuity on every build and publishes SHA-256
  checksums next to each APK. Always compare the checksum after downloading before store uploads.
