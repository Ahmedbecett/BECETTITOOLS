# BECETTI TOOLS v1.1.0 — Softonic resubmission metadata (AR / FR)

Softonic rejected v1.1.0 for two reasons:

1. **Signature** — "the submitted APK file is either not correctly signed or the signature is invalid/corrupted upon verification".
2. **Language consistency** — the submission declares **Arabic** and **French** as supported languages, but the title, caption and description were written in **English**.

This document contains the corrected store metadata in Arabic and French, plus the exact
verification steps for the APK.

| Item | Value |
|---|---|
| Application | BECETTI TOOLS (v1.1.0) |
| Package | `com.becetti.tools` (unchanged) |
| Version | `versionName 1.1.0` · `versionCode 2` |
| Signing identity | `CN=Becetti Tools, O=Becetti, L=Chlef, ST=Chlef, C=DZ` (unchanged since v1.0.0) |
| Certificate SHA-256 | `c40c4ba81a8c28465a126b11fd8fad84cebcf2be163d30ae5c51bbeff75b03b6` |
| APK to upload | `BECETTI-TOOLS-v1.1.0-softonic.apk` |
| SHA-256 | `dda0a1a7de98d215795191cd959570ea5bf0b3738017135b8fd44659cb8f6285` |
| Size | `19,753,917` bytes (18.84 MiB) |
| Signatures | APK Signature Scheme v1 + v2 + v3 — all valid |
| Languages | Arabic (`ar`), French (`fr`), English fallback (`en`) |

> Field limits used below (Softonic publishing form): **Title** 50 characters,
> **Short description / caption** 80 characters, **Full description** 4000 characters.

---

## 1. العربية — Arabic listing

**الاسم / Titre (عربي)**

```text
أدوات بيسيتي
```

**الوصف المختصر / Short description (عربي)**

```text
20 أداة ذكية في تطبيق واحد: حاسبة، عملات، QR، PDF، صور وملاحظات — بدون إنترنت.
```

**الوصف الكامل / Full description (عربي)**

```text
أدوات بيسيتي — كل ما تحتاجه في صندوق أدوات ذكي واحد.

تطبيق عربي بالكامل يعمل بدون إنترنت، يجمع 20 أداة عملية في مكان واحد بتصميم عصري ودعم كامل للواجهة العربية من اليمين إلى اليسار.

■ الحسابات والتحويلات
• الآلة الحاسبة العلمية: الجمع والطرح والضرب والقسمة، النسبة المئوية، الجذور، الأسس، الأقواس، الدوال المثلثية واللوغاريتمات مع سجل محفوظ.
• محول الوحدات الشامل: الطول، الوزن، درجة الحرارة، المساحة، الحجم، السرعة وحجم البيانات.
• محول العملات: 16 عملة عالمية وعربية (منها الدينار الجزائري، الريال السعودي، الدرهم الإماراتي، الجنيه المصري، الدرهم المغربي والدينار التونسي) مع وضع فوري بدون إنترنت.
• حاسبة النسبة والتخفيضات: النسبة من رقم، الزيادة والنقصان، خصومات التسوق والنسب.
• حاسبة العمر والتاريخ: العمر بالسنوات والأشهر والأيام، فارق التواريخ والعد التنازلي لعيد الميلاد.

■ رموز QR والأمان
• استوديو إنشاء رموز QR: روابط، نصوص، شبكة Wi-Fi، جهات اتصال vCard، ألوان مخصصة وتصدير PNG.
• ماسح رموز QR: مسح مباشر بالكاميرا مع الفلاش، أو مسح صورة من المعرض، وفتح الروابط بعد تأكيدك فقط.
• مولّد كلمات المرور: توليد آمن عبر SecureRandom بطول يصل إلى 64 حرفًا مع مؤشر قوة.
• مختبر قوة كلمة المرور: تحليل محلي لحساب قوة كلمة المرور وزمن الاختراق التقديري.

■ المستندات والملفات
• تحويل الصور إلى PDF: دمج عدة صور في ملف PDF واحد بمقاس A4 أو A3 أو Letter.
• دمج ملفات PDF: ضم عدة مستندات في ملف واحد مع الحفاظ على الأصل.
• ضاغط الصور: تقليص حجم الصور بجودة قابلة للتعديل مع معاينة الحجم قبل الحفظ.
• استخراج النص من الصور (OCR): قراءة النصوص من الصور على جهازك دون رفعها إلى الإنترنت.
• نقل الملفات عبر Wi-Fi: إرسال واستقبال الملفات بين جهازك وجهاز آخر على نفس الشبكة المحلية.

■ النصوص والإنتاجية
• أدوات النصوص والترميز: عدّ الكلمات والحروف، تنظيف المسافات، تغيير حالة الأحرف، الترتيب، العكس وترميز Base64.
• مدير الملاحظات: إنشاء وتعديل وحذف وبحث فوري في ملاحظات محفوظة على جهازك فقط.

■ الصحة والوقت والجهاز
• حاسبة الصحة وكتلة الجسم: مؤشر كتلة الجسم، الوزن المثالي، السعرات وشرب الماء.
• ساعة الإيقاف والمؤقت: ساعة إيقاف بدقة الأجزاء مع تسجيل الدورات ومؤقت تنازلي.
• معلومات الجهاز: الذاكرة، التخزين، المعالج، البطارية، الشاشة وإصدار النظام.

■ لماذا أدوات بيسيتي؟
• يعمل بدون إنترنت: معظم الأدوات تعمل بدون اتصال تمامًا، ومحول العملات يحفظ آخر أسعار محدَّثة.
• خصوصيتك أولًا: الصور والملاحظات والملفات تُعالج على جهازك ولا تُرفع إلى أي خادم.
• بدون تعقيد: واجهة عربية بسيطة، بحث سريع، مفضلة وسجل لآخر الأدوات المستخدمة.
• خفيف وسريع: يعمل على Android 7.0 فما فوق.

■ الأذونات
• الكاميرا: تُطلب عند فتح ماسح رموز QR فقط.
• الإنترنت: لتحديث أسعار العملات وللإعلانات المدعومة.
• لا نطلب الوصول إلى جهات الاتصال أو الرسائل أو الموقع الجغرافي.

حمّل أدوات بيسيتي الآن واحصل على 20 أداة في تطبيق واحد.

الكلمات المفتاحية: أدوات، حاسبة، محول عملات، محول وحدات، رمز QR، PDF، ضغط الصور، ملاحظات، حاسبة العمر، كتلة الجسم، أدوات عربية، بدون إنترنت.
```

---

## 2. Français — French listing

**Titre**

```text
BECETTI OUTILS
```

**Description courte (caption)**

```text
20 outils intelligents : calcul, devises, QR, PDF, images et notes — hors-ligne.
```

**Description complète**

```text
BECETTI OUTILS — tout ce dont vous avez besoin dans une seule boîte à outils intelligente.

Une application entièrement en français, qui fonctionne hors-ligne et réunit 20 outils pratiques dans une interface moderne et rapide.

■ Calculs et conversions
• Calculatrice scientifique : additions, soustractions, multiplications, divisions, pourcentages, racines, puissances, parenthèses, fonctions trigonométriques et logarithmes, avec historique conservé.
• Convertisseur d'unités : longueur, poids, température, surface, volume, vitesse et taille de données.
• Convertisseur de devises : 16 devises mondiales et arabes (dinar algérien, riyal saoudien, dirham des Émirats, livre égyptienne, dirham marocain, dinar tunisien…) avec un mode hors-ligne instantané.
• Pourcentages et remises : pourcentage d'un nombre, hausse, baisse, remises commerciales et proportions.
• Calculateur d'âge et de dates : âge exact en années, mois et jours, différence entre deux dates et compte à rebours d'anniversaire.

■ QR codes et sécurité
• Studio générateur de QR codes : liens, textes, réseaux Wi-Fi, contacts vCard, couleurs personnalisées et export PNG.
• Scanner de QR codes : scan caméra en direct avec lampe torche, ou depuis une image de la galerie ; les liens ne s'ouvrent qu'après votre confirmation.
• Générateur de mots de passe : génération cryptographique SecureRandom jusqu'à 64 caractères avec indicateur de robustesse.
• Analyseur de mots de passe : évaluation locale de la robustesse et estimation du temps de cassage.

■ Documents et fichiers
• Images vers PDF : regroupez plusieurs photos dans un seul PDF au format A4, A3 ou Letter.
• Fusionneur PDF : combinez plusieurs documents PDF en un seul fichier, sans modifier les originaux.
• Compresseur d'images : réduisez le poids des photos avec une qualité réglable et un aperçu de la taille finale.
• Extraction de texte OCR : reconnaissance de texte sur vos images, directement sur l'appareil.
• Transfert de fichiers Wi-Fi : envoyez et recevez des fichiers entre deux appareils du même réseau local.

■ Texte et productivité
• Boîte à outils texte et code : comptage des mots et caractères, nettoyage des espaces, changement de casse, tri, inversion et encodage/décodage Base64.
• Gestionnaire de notes : créez, modifiez, supprimez et recherchez instantanément des notes stockées uniquement sur votre appareil.

■ Santé, temps et appareil
• Calculateur IMC et santé : indice de masse corporelle, poids idéal, calories et hydratation.
• Chronomètre et minuteur : chronomètre au centième avec tours et minuteur compte à rebours.
• Infos appareil et système : mémoire RAM, stockage, processeur, batterie, écran et version du système.

■ Pourquoi choisir BECETTI OUTILS ?
• Fonctionne hors-ligne : la plupart des outils sont utilisables sans connexion, le convertisseur de devises conserve les derniers taux.
• Confidentialité d'abord : images, notes et fichiers sont traités sur votre appareil, jamais envoyés sur un serveur.
• Simple et rapide : interface claire, recherche instantanée, favoris et historique des outils récents.
• Léger et compatible : fonctionne à partir d'Android 7.0.

■ Autorisations
• Appareil photo : demandé uniquement lors de l'ouverture du scanner de QR codes.
• Internet : pour actualiser les taux de change et pour les publicités.
• Aucun accès aux contacts, aux SMS ni à la localisation.

Téléchargez BECETTI OUTILS et profitez de 20 outils réunis dans une seule application.

Mots-clés : outils, calculatrice, convertisseur de devises, convertisseur d'unités, QR code, PDF, compression d'images, notes, calculateur d'âge, IMC, outils en français, hors-ligne.
```

---

## 3. What changed for language consistency

| Item | Before (rejected) | After |
|---|---|---|
| Title | `BECETTI TOOLS` (English) | `أدوات بيسيتي` (ar) · `BECETTI OUTILS` (fr) |
| Caption / short description | English | Arabic + French, ≤ 80 characters |
| Full description | English | Arabic + French, ≤ 4000 characters |
| Launcher label (`res/values-ar`, `res/values-fr`) | existed for some strings only | complete Arabic and French resource sets incl. app name, tagline, search hint, permission rationales |
| Tool counter in app UI | said 19 tools for a 20-tool catalog | now 20 in all three languages |

Supporting technical facts behind the metadata (no invented claims):

* **20 tools** — `ToolCatalog.all` (19 tools) plus the Wi-Fi file transfer screen: 20 entries.
* **16 currencies** — `CurrencyApi` currency table.
* **Offline** — `CurrencyScreen` ships built-in reference rates and a DataStore cache; all other tools are on-device.
* **Android 7.0+** — `minSdk 24`.
* **Permissions** — the merged manifest declares only `CAMERA`, `INTERNET`, `ACCESS_NETWORK_STATE`.

---

## 4. Signature verification (the other rejection reason)

The v1.1.0 binary is correctly signed with the same certificate as v1.0.0. Independent
verification of every layer (JAR/v1, v2 and v3) passes:

```bash
pip install cryptography asn1crypto
python3 tools/verify_apk_signature.py BECETTI-TOOLS-v1.1.0-softonic.apk
```

Expected result: `OVERALL: VALID — APK signature verified`.

If Android build-tools are available, the standard check is:

```bash
apksigner verify --verbose --print-certs BECETTI-TOOLS-v1.1.0-softonic.apk
```

A valid APK can still fail a store-side check when the uploaded file is not byte-identical
to the built one (interrupted download, chat-app re-compression, re-zipping). Always compare
the SHA-256 before uploading:

```bash
sha256sum BECETTI-TOOLS-v1.1.0-softonic.apk
# dda0a1a7de98d215795191cd959570ea5bf0b3738017135b8fd44659cb8f6285
```

The build itself can no longer produce a debug-signed "release" APK: `app/build.gradle.kts`
fails the build when `keystore/becetti-release.jks` is missing instead of silently falling
back to the debug keystore.

---

## 5. Resubmission checklist

1. Replace the APK with `BECETTI-TOOLS-v1.1.0-softonic.apk` and confirm size + SHA-256.
2. Set the app title to `أدوات بيسيتي` for the Arabic listing and `BECETTI OUTILS` for the French listing.
3. Paste the Arabic caption/description into the Arabic field and the French ones into the French field.
4. Remove or translate any remaining English title/caption/description text in the submission form.
5. Keep the supported-language selection: Arabic + French.
6. Submit for review.
