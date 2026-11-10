# دليل التوافق مع شروط وسياسات متجر أمازون (Amazon Appstore Compliance Guide)

**التطبيق:** BECETTI TOOLS (`com.becetti.tools`)  
**الإصدار المنشور حاليًا:** `1.0.0` (`versionCode = 1`) — لا يزال هو إصدار Amazon الحالي.

**إصدار المصدر المرشح:** `1.1.0` (`versionCode = 2`) — بيانات Gradle محدّثة؛ لم يُبنَ APK بعد ولم يُرسل إلى Amazon. يلزم اجتياز البناء والاختبارات قبل اعتباره إصداراً جاهزاً للنشر.

---

## 1. ملخص شروط وسياسات Amazon Appstore وكيف تم تطبيقها

### أ) حجم ملف الـ APK (أقل من 50 ميغابايت)
- **شرط أمازون الرسمي ([Understanding Amazon Appstore Submission](https://developer.amazon.com/docs/app-submission/understanding-submission.html)):**
  توصي أمازون بأن يكون حجم ملف الـ APK **50 ميغابايت أو أقل** لتجنب رسالة التحذير في لوحة مطوري أمازون (`You have uploaded a larger binary (>50MB)`), وتسريع التحميل والتثبيت على أجهزة Fire OS وأندرويد.
- **ما تم تطبيقه في النسخة النهائية:**
  1. تحويل البناء من `Debug` (الذي كان بحجم **88.87 MB**) إلى نسخة **Release نهائية موقّعة** (`isMinifyEnabled = true`, `isShrinkResources = true` عبر R8/ProGuard).
  2. إزالة مكتبة `material-icons-extended` غير الضرورية التي كانت تضيف **41.5 MB** من ملفات DEX واستبدال الأيقونات الثلاث بمتجهات أصلية خفيفة (`BecettiIcons.kt`).
  3. تصفية معماريات المعالجات (`abiFilters`) لتشمل معماريات أجهزة Fire OS وأندرويد الفعلية (`armeabi-v7a` 32-bit و`arm64-v8a` 64-bit) وحذف ملفات المحاكيات `x86` و`x86_64` التي كانت تستهلك أكثر من 33 MB.
  4. ضغط المكتبات الأصلية داخل الـ APK (`useLegacyPackaging = true`) واستبعاد جداول التشفير الكمومي غير المستخدمة (`org/bouncycastle/pqc/**` بحجم 8.2 MB).
  5. تجريد رموز التصحيح (`debugSymbolLevel = "NONE"`).

### ب) التوقيع الرقمي وتوافق Fire OS 7 / Fire OS 8
- **شرط أمازون الرسمي ([Fire OS 8 Requirements](https://developer.amazon.com/docs/fire-tv/fire-os-8.html)):**
  - يرفض متجر أمازون ملفات APK التي تحمل `android:debuggable="true"`.
  - يشترط التوقيع بمخطط **APK Signature Scheme v1 + v2** معاً لضمان التوافق مع أجهزة Fire OS 7 والأقدم (التي تعتمد v1) وأجهزة Fire OS 8 والأحدث (التي تشترط v2).
- **ما تم تطبيقه:**
  - `isDebuggable = false` ومعرّف الحزمة الرسمي `com.becetti.tools` (بدون اللاحقة `.debug`).
  - توقيع رسمي بمفتاح إصدار (`keystore/becetti-release.jks`) مع تفعيل `enableV1Signing = true` و`enableV2Signing = true` و`enableV3Signing = true`.
  - تظل الأدوات الأساسية متاحة على Fire OS؛ أما إعلانات Google فتحتاج اختبارًا على أجهزة Fire OS التي قد لا تتضمن Google Play services، ولا نضمن عرض الإعلانات عليها. أضيف نقل الملفات المحلي عبر Wi-Fi مع بقاء ملفات المستخدم ضمن اختيارات SAF فقط، مع دعم `CoreLibraryDesugaring` ومعالجة مسارات الحفظ القديمة.

### ج) فلاتر الـ Manifest والأذونات (Manifest Filters & Permissions Hygiene)
- **شرط أمازون الرسمي ([Manifest Filters Supported by the Amazon Appstore](https://developer.amazon.com/docs/app-submission/supported-filters-on-the-amazon-appstore.html)):**
  - أي إذن عتاد مثل `android.permission.CAMERA` يفترض متجر أمازون أنه إلزامي ويحجب التطبيق عن الأجهزة اللوحية التي لا تملك كاميرا خلفية أو تركيزاً تلقائياً ما لم يُصرّح بـ `android:required="false"`.
  - إزالة الأذونات غير المستخدمة.
- **ما تم تطبيقه في `AndroidManifest.xml`:**
  - إضافة `<uses-feature android:name="android.hardware.camera" android:required="false" />` و`camera.autofocus` و`touchscreen` بقيمة `required="false"`.
  - إضافة `<supports-screens>` لدعم جميع أحجام الشاشات (`small`, `normal`, `large`, `xlarge`) لأجهزة الهواتف وأجهزة Fire HD اللوحية.
  - حذف أذونات `READ_MEDIA_IMAGES` و`READ_MEDIA_VISUAL_USER_SELECTED` لأن اختيار الصور وملفات PDF يتم عبر منتقي النظام الآمن (SAF) دون الحاجة لأذونات تخزين واسعة.
  - فرض الاتصال المشفر `android:usesCleartextTraffic="false"`.

### د) سياسة المحتوى والإعلانات والخصوصية (Content, Advertising & Privacy Policies)
- **شرط أمازون الرسمي ([Amazon Appstore Content Policy](https://developer.amazon.com/docs/policy-center/understanding-content-policy.html) & [Advertising Policies](https://developer.amazon.com/docs/policy-center/amazon-appstore-advertising.html)):**
  - خلو التطبيق من أي أزرار وهمية أو عناصر إعلانية غير فعالة أو مضللة.
  - توفير سياسة خصوصية واضحة داخل التطبيق وفي صفحة المتجر، مع آلية موافقة المستخدم وحذف البيانات المحلية.
- **ما تم تطبيقه:**
  - إخفاء لافتة `AdPlaceholder` تلقائياً طالما لم يتم تفعيل رابط إعلاني فعلي (`adsterra.isAvailable() == false`) لضمان عدم وجود أي عنصر غير فعال أثناء فحص أمازون.
  - دمج Google UMP لتحديث حالة الموافقة عند بدء التطبيق، وعرض مدخل خيارات الخصوصية في الإعدادات فقط عندما يطلبه UMP. يتطلب ظهور رسائل الموافقة إعداد الرسائل المناسبة في حساب AdMob؛ ولا يُعد الدمج وحده إثباتاً للامتثال القانوني.
  - إضافة عارض **سياسة الخصوصية داخل التطبيق (In-App Privacy Policy Dialog)** في شاشة الإعدادات يعمل بدون إنترنت وعلى أجهزة Fire OS التي قد لا تحتوي على متصفح خارجي.

---

## 2. الأصول الجاهزة للرفع في لوحة مطوري أمازون (Store Assets)

1. **ملف التطبيق المنشور حاليًا:**
   - `BECETTI-TOOLS-v1.0.0-release.apk` هو ملف الإصدار السابق فقط. لم يُنتج APK للإصدار المرشح `v1.1.0`؛ يجب إعادة قياس الحجم والتحقق من التوقيع والتوافق قبل التقديم.
2. **الأيقونات الرسمية المطلوبة في متجر أمازون:**
   - `docs/amazon/icon-512x512.png` (أيقونة 512×512 PNG بخلفية شفافة للحواف).
   - `docs/amazon/icon-114x114.png` (أيقونة 114×114 PNG).
3. **رابط سياسة الخصوصية لصفحة المتجر:**
   - `https://github.com/Ahmedbecett/BECETTITOOLS/blob/main/docs/PRIVACY_POLICY.md`
