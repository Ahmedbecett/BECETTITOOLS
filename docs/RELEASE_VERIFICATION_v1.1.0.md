# BECETTI TOOLS v1.1.0 — final verification record

Prepared for Softonic resubmission. Everything below was executed on the released binaries in
this repository; each row names the tool that produced the result.

## 1. Deliverables

| File | Size (bytes) | SHA-256 |
|---|---|---|
| `BECETTI-TOOLS-v1.1.0-softonic.apk` (upload this one) | 19,753,918 | `80417b761cdafa9c11e765cf9df8e5d53ae2ef041917b7bc528e6041413c0c2c` |
| `BECETTI-TOOLS-v1.1.0-release.apk` (pristine CI build) | 19,767,102 | `b16282dbe460cb9892da990595b5eeec03250d28e1b83796e721bbfdfc581eda` |
| `BECETTI-TOOLS-v1.1.0-softonic.apk.sha256` | checksum file | — |
| `BECETTI-TOOLS-v1.1.0-release.apk.sha256` | checksum file | — |

Store metadata: `docs/store/softonic-v1.1.0-metadata.json` (Arabic + French) and
`docs/STORE_METADATA_AR_FR.md` (same texts, formatted for the submission form).

## 2. Identity (must not change between releases)

| Property | Value |
|---|---|
| Package | `com.becetti.tools` |
| Version | `versionName 1.1.0`, `versionCode 2` |
| minSdk / targetSdk / compileSdk | 24 / 34 / 35 |
| Signer certificate subject | `CN=Becetti Tools, OU=Mobile Development, O=Becetti, L=Chlef, ST=Chlef, C=DZ` |
| Signer certificate SHA-256 | `c40c4ba81a8c28465a126b11fd8fad84cebcf2be163d30ae5c51bbeff75b03b6` |
| Keystore | `keystore/becetti-release.jks` (PKCS#12, alias `becetti`, RSA 2048, valid to 2054-03-26) |
| Same certificate as v1.0.0 | yes → existing installs update in place |

## 3. Signature verification

### 3.1 Independent verifier (no Android tooling)

```
$ python3 tools/verify_apk_signature.py BECETTI-TOOLS-v1.1.0-softonic.apk
CompiledResults... entries: 649
APK Signing Block: present (0x7109871a (v2), 0xf05368c0 (v3))

--- APK Signature Scheme v1 (JAR signing) ---
  MANIFEST.MF entry digests (646 entries): PASS
  every ZIP entry covered by MANIFEST.MF: PASS
  CERT.SF sha256 digest of MANIFEST.MF: PASS
  CERT.SF per-entry digests of MANIFEST.MF sections (646 checks): PASS
  CERT.RSA PKCS#7 signature over CERT.SF: PASS
  APK Signature Scheme v1: PASS

--- APK Signature Scheme v2 ---
  signer #1 signature [RSASSA-PKCS1-v1_5 / SHA-256]: PASS
  signer #1 content digest: PASS
  signer #1 certificate matches embedded public key: PASS
  APK Signature Scheme v2: PASS

--- APK Signature Scheme v3 ---
  signer #1 signature [RSASSA-PKCS1-v1_5 / SHA-256]: PASS
  signer #1 content digest: PASS
  signer #1 SDK range: 24..2147483647
  APK Signature Scheme v3: PASS

  all schemes signed by one certificate: PASS

OVERALL: VALID — APK signature verified
```

What the verifier actually checks, per scheme:

* **v1 (JAR):** SHA-256 digest of every ZIP entry against `MANIFEST.MF`; `MANIFEST.MF` digest
  against `CERT.SF`; every per-entry section digest; the PKCS#7 (RSA/SHA-256) signature in
  `CERT.RSA` over `CERT.SF`; and that `MANIFEST.MF` covers every entry.
* **v2/v3:** the chunked content digest over all three APK sections (content before the signing
  block, the central directory, the EOCD with the signing-block offset patched in); the RSA
  signature over the signed-data block; that the certificate embeds the signing key; certificate
  validity dates; and — for v3 — the SDK range.
* **Cross-scheme:** v1, v2 and v3 must be signed by one and the same certificate.

### 3.2 Android `apksigner` (CI, every build)

`apksigner verify --verbose --print-certs` runs on every CI build, followed by
`tools/verify_apk_signature.py`, followed by a comparison of the signer certificate with the
shipped v1.0.0 APK. All steps passed in run
[45604757481](https://github.com/Ahmedbecett/BECETTITOOLS/actions/runs/45604757481).

### 3.3 Tamper detection (the real cause of "corrupted upon verification")

Copies of the submission APK were deliberately damaged and re-verified:

| Variant | Result |
|---|---|
| truncated by 1,000 bytes | `INVALID` — "the file is truncated or corrupted, not a valid APK" |
| one bit flipped inside `classes.dex` | `INVALID` — v1 Bad CRC-32, v2 digest FAIL, v3 digest FAIL |
| 3 appended bytes | `INVALID` — v2 digest FAIL, v3 digest FAIL, v1 PASS |

This is why the uploaded file must be byte-identical to the one in this repository: any change
to the bytes — even a re-zip or a chat-app re-compression — invalidates the signature.

## 4. Package integrity

| Check | Result |
|---|---|
| ZIP container integrity, 649 entries | PASS |
| `package com.becetti.tools`, `versionCode 2`, `versionName 1.1.0` | PASS |
| `debuggable = false` | PASS |
| `minSdk 24`, `targetSdk 34` | PASS |
| Size 18.84 MiB (< 50 MB store guideline) | PASS |
| Release and Softonic APKs have identical entry sets and content (0 differing entries) | PASS |
| Softonic APK signing block contains only the v2 and v3 pairs | PASS |

## 5. Language consistency

| Check | Result |
|---|---|
| Arabic listing: Arabic script, ≤ 50/80/4000 characters | PASS (title 12, caption 78, description 2,745 chars) |
| French listing: Latin script with French accents, ≤ 50/80/4000 characters | PASS (title 14, caption 80, description 3,642 chars) |
| No English boilerplate in either listing | PASS |
| JSON metadata mirrored in the markdown document | PASS |
| `values-ar` / `values-fr` complete, genuinely translated, launcher label localized | PASS |
| Tool counter matches the 20-tool catalog in all three languages | PASS |

The shipped APK contains the Arabic launcher label `أدوات بيسيتي`, the French label
`BECETTI OUTILS`, 331 Arabic strings and the corrected "20 tools" counters (verified inside
`resources.arsc` and the DEX of the released file).

## 6. Changes that prevent a repeat

1. `app/build.gradle.kts` fails the build when `keystore/becetti-release.jks` is missing; a
   `taskGraph.whenReady` guard blocks every `*Release*` task in that case. The old code silently
   signed release APKs with the debug key — exactly the "not correctly signed" failure.
2. New `verifyReleaseApk` Gradle task runs `apksigner verify` and fails the build on any
   verification error. CI builds through this task.
3. CI asserts the keystore fingerprint `C40C4BA8…03B6` before building, so a swapped keystore
   cannot produce a release artefact.
4. CI runs two independent signature verifiers and compares the signer certificate with v1.0.0.
5. CI validates the Arabic/French metadata and resources before the build, so an English-only or
   incomplete listing cannot be published again.
6. `tools/make_softonic_apk.py` produces the submission APK deterministically and refuses to
   write it unless verification passes.

## 7. Sign-off checklist

- [x] Release APK built from a clean CI build of this branch
- [x] Signed with the original release keystore (not debug), certificate unchanged since v1.0.0
- [x] APK Signature Scheme v1 + v2 + v3 verified by `apksigner` and by an independent verifier
- [x] Package name, version code and version name unchanged as required
- [x] Application functionality preserved (no feature removed, no demo or mock content added)
- [x] Arabic and French titles, captions and descriptions provided and length-checked
- [x] SHA-256 checksums published next to both APKs
- [ ] Upload `BECETTI-TOOLS-v1.1.0-softonic.apk` to Softonic after comparing its SHA-256
- [ ] Paste the Arabic and French texts into the corresponding listing fields
