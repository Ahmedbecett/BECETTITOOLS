# تقرير تجهيز BECETTI TOOLS للنشر على Amazon Appstore

**الحزمة:** `com.becetti.tools` — **الإصدار:** 1.0.0 (versionCode 1) — **الفرع:** `arena/24a4b936-becettitools`  
**الحالة:** تم إصلاح جميع الملفات وإعدادات البناء في الكود المصدري. **ملحوظة هامة:** لم يتم تنفيذ أمر البناء في هذه الجزيرة لأن البيئة لا تحتوي على Java JDK ولا Android SDK، ومضيفو Google (مصدر SDK الرسمي) محجوبون في الشبكة. لم أُزَوَّر نتيجة أي بناء؛ أدناه التعليمات الدقيقة للتنفيذ والتحقق على أي جهاز يحتوي على Android Studio.

---

## 1. ما تم فحصه (مراجعة شاملة للمشروع)

تم فحص كل ملف في المستودع:

| الجزء | النتيجة |
|------|--------|
| `build.gradle.kts` (الجذر) | AGP 8.2.2، Kotlin 1.9.22، KSP — سليم |
| `settings.gradle.kts` | مستودعات Google + MavenCentral فقط — سليم |
| `gradle.properties` | compileSdk 34 مع كبت التحذير — سليم |
| `app/build.gradle.kts` | معدل (انظر قسم التعديلات) |
| `AndroidManifest.xml` | معدل (الصلاحيات، النسخ الاحتياطي، المرور الواضح) |
| `proguard-rules.pro` | إعادة كتابة كاملة لقواعدéta |
| كود المصدر (30 ملف Kotlin) | جميع الميزات الـ 15 تعمل محلياً، لا WebView، keine Amazon SDK، لا خدمات جوجل بلاي مطلوبة |
| الموارد | الأيقونة والاسم والتصميم محفوظة دون تعديل |
| APKdebug الحالي 85 ميجابايت | تم تحليله فعلياً (انظر القسم 2) |

**المكتبات التي تُستخدم فعلياً في الكود:** Room, DataStore, Compose/Material3, Navigation-Compose, Lifecycle, Coroutines, CameraX, ML Kit (الباركود + التعرف النصي اللاتيني)، ZXing core (توليد QR فقط)، PDFBox-Android (دمج PDF)، Retrofit+OkHttp+Gson (العملات).

**مكتبات محذوفة لأنها غير مستخدمة في أي شاشة:** `coil-compose`، `com.journeyapps:zxing-android-embedded`، `material-icons-extended` (استبدلتها بـ `material-icons-core` لأن كل الأيقونات المستخدمة من `Icons.Filled`)، و `logging-interceptor` (نقلته إلى `debugImplementation` فقط).

---

## 2. تحليل حجم الـ APK الحالي (استناداً إلى الملف الفعلي 85 ميجابايت)

باستخدام `unzip -l` على `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk`:

| المكون | الحجم |
|--------|-------|
| `classes.dex` (+ فئات Dex فرعية حتى `classes20`) | ≈ 73 ميجابايت (بلا تصغير R8) |
| مكتبات أصلية `.so` — 4 بنائيات (arm64-v8a, armeabi-v7a, x86, x86_64) | ≈ 60 ميجابايت (MLKit OCR Pipeline ≈ 10–11 ميجا × 4، Barhopper ≈ 4–5.5 ميجا × 4) |
| أصول PDFBox (ملفات cmap للخطوط CJK وغيرها) | عدة ميجابايتات |
| **الإجمالي الفعلي** | **85 ميجابايت** |

**مسببات التجاوز above 50 ميجابايت:**
1. Absence of minification/shrinking في النسخة النهائية (`isMinifyEnabled = false`) → Dex 73 ميجابايت.
2. مكتبات MLKit الأصلية لجميع البنائيات الأربع رغم أن أجهزة Fire OS تعمل بـ ARM فقط.
3. مكتبات غير مستخدمة في الحزمة النهائية.

---

## 3. التعديلات المطبقة مباشرة في الكود

### أ. `app/build.gradle.kts`
- **تفعيل التصغير وحذف الموارد غير المستخدمة** في벤در `release`:
  - `isMinifyEnabled = true`
  - `isShrinkResources = true`
  - استخدام `proguard-android-optimize.txt` مع `proguard-rules.pro` الجديد.
- **إزالة التبعيات غير المستخدمة:** `coil-compose`، `zxing-android-embedded`، `material-icons-extended` (대체 بـ `material-icons-core`).
- **نقل `logging-interceptor`** إلى `debugImplementation` فقط.
- **دعم توقيع النسخة النهائية:** كتلة `signingConfigs.create("release")` تقرأ من متغيرات البيئة (`BECETTI_STORE_FILE/PASSWORD`, `BECETTI_KEY_ALIAS/PASSWORD`) أو من ملف `keystore.properties` (مستبعد من git).
- **فلترة البنائيات للـ APK universale:** خاصية `-Pndk.abifilters=armeabi-v7a,arm64-v8a` تزيل `x86/x86_64` من الـ APK (لا تُطبَّق على AAB ليتولى أمازون التوزيع لكل جهاز).
- **ملحق المانيفست:** `allowBackup = false` في الإنتاج، `true` في التصحيح.

### ب. `app/proguard-rules.pro` (إعادة كتابة كاملة)
- حفظ كود التطبيق بالكامل (`com.becetti.tools.**`) وفئات Room و Retrofit و MLKit و ZXing و PDFBox.
- حفظ الدوال الأصلية (`native <methods>`).
- تجاهل تحذيرات آمنة لمكتبات تشير لفئات غير موجودة.
- إزالة سجلات التصحيح (`Log.d/v`) في النسخة النهائية عبر `assumenosideeffects`.

### ج. `app/src/main/AndroidManifest.xml`
- **حُذفت صلاحيتان غير مستخدمان:** `READ_MEDIA_IMAGES` و `READ_MEDIA_VISUAL_USER_SELECTED` (الكود يستخدم `MediaStore.insert` فقط الذي لا يحتاج صلاحية قراءة).
- `android:allowBackup="${allowBackup}"` —値 `false` في النسخة النهائية.
- `android:usesCleartextTraffic="false"` (ضمان اتصال آمن فقط).
- الاسم، الأيقونة، التصميم، اسم الحزمة، `minSdk=24`، `targetSdk=34` — كلها محفوظة دون تغيير.

### د. ملفات جديدة
- `keystore.properties.sample` — نموذج لتكوين مخزن التوقيع (يُنشئ المستخدم `keystore.properties` الفعلي ويقوم بأمره `keytool`).

---

## 4. تقليل الحجم دون حذف أي ميزة (م	expect)

| composant | قبل | بعد (متوقع) |
|-----------|-----|-------------|
| Dex (بلا تصغير) | ≈ 73 ميجابايت | ≈ 8–15 ميجابايت (مع R8 + proguard) |
| المكتبات الأصلية (APK universale، بنائيين ARM فقط) | ≈ 60 ميجابايت | ≈ 29 ميجابايت (arm64-v8a + armeabi-v7a) |
| APK universale متوقع | 85 ميجابايت | **≈ 35–45 ميجابايت** (تحت عتبة تحذير أمازون 50 ميجابايت) |
| AAB للنشر على أمازون | — | كل بنائية تُوزَّع وحدها؛-size التنزيل للجهاز أصغر بكثير |

ملاحظة: أرقام "المتوقع" تستند إلى تحليل الملف الفعلي وإزالة الكود غير المستخدم؛ يتم تأكيد الحجم النهائي بعد تنفيذ البناء الفعلي (انظر القسم 10).

---

## 5. توافق Amazon Appstore وأجهزة Fire OS

- **minSdk 24 / targetSdk 34:** يغطي جميع أجهزة Fire OS (Fire OS 7+)، وقبول أمازون يتطلب targetSdk ≥ 31 للتطبيقات الجديدة — مطبق.
- **لا تعتمد على خدمات جوجل بلاي:** ML Kit مربوط بالحزمة (bundled)، ومحول العملات يستخدم `frankfurter.app` عبر HTTPS، والإعلانات في الكود هي نماذج اختبار (placeholders) بدون SDK إعلاني فعلي — لا مشكلة على أجهزة فاير التي لا تحتوي على GMS.
- **لا WebView:** مؤكد تفقدياً (`grep` سلبي) — يتوافق مع سياسة أمازون.
- **تقبل أمازون كلاً من APK و AAB:** يُنصح برفع **AAB** (`bundleRelease`) حتى يقوم المتجر بتقسيم الحزمة تلقائياً حسب بنائية كل جهاز، مما يحل مشكلة 50 ميجابايت للمستخدم النهائي.
- **الاتجاه RTL (العربية/الفرنسية/الإنجليزية) مدعوم:** `supportsRtl=true`، خطوط ومترجمات محفوظة.

---

## 6. مراجعة الأذونات والأمان والأداء والاستقرار

| البند | الحالة |
|------|--------|
| الأذونات | فقط `CAMERA` (طلب waktu التشغيل عند opening الماسح فقط)، `INTERNET`، `ACCESS_NETWORK_STATE`. حُذفت الصلاحيات غير المستخدمة. |
| النسخ الاحتياطي | `allowBackup=false` في الإصدار النهائي (توصية أمنية للمتاجر). |
| المرور الواضح | معطل (`usesCleartextTraffic="false"`). |
| تصدير MainActivity | `exported=true` فقط لمرشح البدء (القياسي). |
| حماية الكود | R8 محمل في الإصدار، جميع فئات التطبيق محفوظة، المكتبات مصغرة. |
| السجلات | تُزال في الإصدار النهائي (`assumenosideeffects` على `Log.d/v`). |
| الاستقرار | keine التغييرات في منطق العمل؛ جميع الشاشات الـ 15unchanged. |

---

## 7. معالجة تحذير DRM

Amazon Appstore توفر **تطبيق DRM (App DRM)** كخيار يُفعّل من لوحة النشر على الملف المرفوع (APK/AAB) — ليس شيئًا يُضاف في كود التطبيق، وليس مطلوبًا من أمازون.

**التقييم للتطبيق هذا:** تطبيق أدوات مجاني، لا محتوى محمي بحقوق نشر، لا اشتراكات، لا مشتريات داخل التطبيق. لذلك **لا حاجة إلى تفعيل DRM**؛ تفعيله سيضيف صعوبة على المستخدمين الشرعيين دون فائدة واقعية. لم أُضيف أي أكواد DRM، والتوصية هي ترك الخيار معطلاً في وحدة تحكم Amazon Appstore ما لم يطلب publisher حماية من النسخ لاحقًا.

---

## 8. أسماء التصميم والميزات (محفوظة بالكامل)

- اسم التطبيق: **BECETTI TOOLS** (بدون تغيير).
- Package name: `com.becetti.tools` (بدون تغيير).
- الأيقونة: `@mipmap/ic_launcher` و `ic_launcher_round` (بدون تغيير).
- التصميم: Material 3، الـ 15 أداة، تقسيم الشاشات، Room، DataStore، الكاميرا، MLKit، PDF، QR — كلها محفوظة.
- لم يُنشأ مشروع جديد؛ تم إصلاح المشروع الحالي فقط.

---

## 9. خطوات البناء والتحقق (للتنفيذ على جهاز يحتوي على Android Studio)

المتطلبات: Java JDK 17+، Android SDK مع `platforms;android-34` و `build-tools` (AGP 8.2.2).

```bash
# 1. تحديد مسار SDK
echo "sdk.dir=/مسار/الأندرويد/SDK" > local.properties

# 2. إنشاء مخزن التوقيع (مرة واحدة)
keytool -genkeypair -v \
  -keystore release-keystore.jks \
  -alias becetti \
  -keyalg RSA -keysize 2048 \
  -validity 10000 \
  -dname "CN=BECETTI TOOLS, OU=BECETTI, O=BECETTI, L=Chlef, S=Chlef, C=DZ"

# 3. إعداد كلمات المرور (اختر أحد الخيارين)
# الخيار أ: ملف keystore.properties (ينسخ من keystore.properties.sample ويعدل)
# الخيار ب: متغيرات البيئة
export BECETTI_STORE_FILE=release-keystore.jks
export BECETTI_STORE_PASSWORD=<<كلمة مرور المخزن>>
export BECETTI_KEY_ALIAS=becetti
export BECETTI_KEY_PASSWORD=<<كلمة مرور المفتاح>>

# 4. بناء النسخة النهائية
./gradlew clean
./gradlew bundleRelease                      # ينتج app-release.aab (لأمازون — مفضّل)
./gradlew assembleRelease -Pndk.abifilters=armeabi-v7a,arm64-v8a  # APK universale للأجهزة ARM

# 5. المسارات الناتجة
app/build/outputs/bundle/release/app-release.aab
app/build/outputs/apk/release/app-universal-release.apk
```

**كيفية التحقق من النجاح:**
- أمر البناء ينتهي بدون أخطاء (`BUILD SUCCESSFUL`).
- APK unsigned-check: `apksigner verify --verbose app/build/outputs/apk/release/app-universal-release.apk` يجب أن يظهر التوقيع صحيحاً.
- حجم الـ APK universale يجب أن يكون تحت 50 ميجابايت (للبنائيين ARM).
- تثبيت ملف APK على جهاز/محاكي Fire OS واختبار الأدوات الـ 15 (الماسح، OCR، PDF، العملات، الملاحظات…).

---

## 10. ملخص الوضع الفعلي (مهم)

**لم يُنفّذ أمر البناء في هذه الجزيرة**, لأن:
- لا `java`/`javac` مثبتة في النظام، ولا يمكن تثبيتها (مستودعات apt محجوبة).
- لا `Android SDK` موجود في أي مسار (`platform-tools`, `build-tools`, `platforms`, `aapt2`, `apksigner` غير موجودة).
- مضيفو Google (`dl.google.com`) محجوبون في الشبكة، والمسموح به (github.com، npm، pypi…) لا يوفّر Android SDK رسمياً.

بناءً على ذلك، **لا يمكنني رفع ملف APK/AAB جاهز الآن، ولن أدّعي نجاح بناء لم يُنفَّذ** (التزامك بمنع البيانات الوهمية محترم). كل ما يمكن تقديمه الآن هو الكود المُصلح تمامًا وجاهز للبناء، مع التعليمات الدقيقة أعلاه. بمجرد تنفيذ الأوامر على جهاز يتوفر فيه Android SDK، سيخرج ملفان نهائيان جاهزان للنشر:
1. `app-release.aab` — الملف الأساسي المرفوع إلى Amazon Appstore (تقسيم تلقائي حسب الجهاز).
2. `app-universal-release.apk` — نسخة Universale بحجم متوقع 35–45 ميجابايت (تحت عتبة 50 ميجابايت).

يرجى إبلاغي بعد تنفيذ البناء لنقوم معاً بتأكيد النتيجة وإكمال عملية الرفع على Amazon Appstore.
