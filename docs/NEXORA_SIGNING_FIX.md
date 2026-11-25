# إصلاح توقيع Nexora — جاهز للتطبيق على `nexora-social-2026`

الإصلاح يمنع البناء من التوقيع الصامت بمفتاح التصحيح (debug) الذي ترفضه المتاجر
بسبب "not correctly signed". مُجهَّز بالكامل ومُتحقَّق منه — طبّقه بأي طريقتين:

## الطريقة 1 — من محرّر GitHub (أسهل، بدون أدوات)

### الملف 1: `android/app/build.gradle`

افتح: <https://github.com/Ahmedbecett/nexora-social-2026/edit/main/android/app/build.gradle>

استبدل هذا القسم بالكامل (من سطر تعليق "توقيع نسخة الإصدار" حتى نهاية `buildTypes`):

**القديم (احذفه):**
```groovy
    // توقيع نسخة الإصدار: إن وُفّرت متغيرات البيئة (NEXORA_KEYSTORE / NEXORA_KEYSTORE_PASSWORD)
    // يُوقَّع بمفتاح الإنتاج (كما في GitHub Actions)؛ وإلا يُوقَّع بمفتاح التصحيح حتى تُبنى
    // حزمة تثبيت تلقائيًا على كل الأجهزة دون أسرار.
    def releaseStore = System.getenv("NEXORA_KEYSTORE")
    def releasePass = System.getenv("NEXORA_KEYSTORE_PASSWORD")
    def releaseAlias = System.getenv("NEXORA_KEY_ALIAS") ?: "nexora"
    def useReleaseKeystore = (releaseStore && releasePass)
    signingConfigs {
        release {
            if (useReleaseKeystore) {
                storeFile file(releaseStore)
                storePassword releasePass
                keyAlias releaseAlias
                keyPassword (System.getenv("NEXORA_KEY_PASSWORD") ?: releasePass)
            }
        }
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            if (useReleaseKeystore) {
                signingConfig signingConfigs.release
            } else {
                // لا مفتاح إنتاج مضبوط على هذه البيئة → توقيع تصحيح حتى تُبنى حزمة تثبيت تلقائيًا
                signingConfig signingConfigs.debug
            }
        }
    }
```

**الجديد (الصقه):**
```groovy
    // توقيع نسخة الإصدار: إن وُفّرت متغيرات البيئة (NEXORA_KEYSTORE / NEXORA_KEYSTORE_PASSWORD)
    // يُوقَّع بمفتاح الإنتاج (كما في GitHub Actions). وإلا يفشل البناء بوضوح — لأن التوقيع
    // بمفتاح التصحيح يرفضه Softonic/Amazon/Play برسالة "not correctly signed"، ويربك
    // استمرارية التوقيع بين التحديثات. للبناء المحلي التجريبي فقط: NEXORA_ALLOW_DEBUG_SIGNING=1
    def releaseStore = System.getenv("NEXORA_KEYSTORE")
    def releasePass = System.getenv("NEXORA_KEYSTORE_PASSWORD")
    def releaseAlias = System.getenv("NEXORA_KEY_ALIAS") ?: "nexora"
    def useReleaseKeystore = (releaseStore && releasePass)
    def allowDebugSigning = (System.getenv("NEXORA_ALLOW_DEBUG_SIGNING") == "1")
    signingConfigs {
        release {
            if (useReleaseKeystore) {
                storeFile file(releaseStore)
                storePassword releasePass
                keyAlias releaseAlias
                keyPassword (System.getenv("NEXORA_KEY_PASSWORD") ?: releasePass)
            }
        }
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            if (useReleaseKeystore) {
                signingConfig signingConfigs.release
            } else if (allowDebugSigning) {
                // نسخة اختبارية محلية بمفتاح التصحيح — لا ترفعها إلى أي متجر إطلاقًا
                signingConfig signingConfigs.debug
            } else {
                throw new GradleException(
                    "لا يوجد مفتاح إنتاج (NEXORA_KEYSTORE / NEXORA_KEYSTORE_PASSWORD). " +
                    "رفض التوقيع بمفتاح التصحيح: المتاجر (Softonic/Amazon/Play) ترفضه برسالة " +
                    "'not correctly signed'. للبناء المحلي التجريبي فقط: NEXORA_ALLOW_DEBUG_SIGNING=1"
                )
            }
        }
    }
```

ثم **Commit changes**.

### الملف 2: `.github/workflows/android-release.yml`

افتح: <https://github.com/Ahmedbecett/nexora-social-2026/edit/main/.github/workflows/android-release.yml>

استبدل خطوة "Restore signing key (optional)" كاملة:

**القديم (احذفه):**
```yaml
      - name: Restore signing key (optional)
        env:
          KEYSTORE_B64: ${{ secrets.NEXORA_KEYSTORE_BASE64 }}
        run: |
          if [ -n "$KEYSTORE_B64" ]; then
            echo "$KEYSTORE_B64" | base64 -d > $RUNNER_TEMP/nexora.jks
            echo "NEXORA_KEYSTORE=$RUNNER_TEMP/nexora.jks" >> $GITHUB_ENV
          else
            echo "::warning::لا يوجد NEXORA_KEYSTORE_BASE64 — سيُوقَّع APK بمفتاح التصحيح. لا ترفعه إلى Amazon Appstore."
          fi
```

**الجديد (الصقه):**
```yaml
      - name: Restore signing key (required for publish builds)
        env:
          KEYSTORE_B64: ${{ secrets.NEXORA_KEYSTORE_BASE64 }}
        run: |
          if [ -n "$KEYSTORE_B64" ]; then
            echo "$KEYSTORE_B64" | base64 -d > $RUNNER_TEMP/nexora.jks
            echo "NEXORA_KEYSTORE=$RUNNER_TEMP/nexora.jks" >> $GITHUB_ENV
          elif [ "${{ github.event_name }}" = "pull_request" ]; then
            echo "::warning::لا يوجد NEXORA_KEYSTORE_BASE64 — بناء PR تجريبي فقط بمفتاح التصحيح (غير قابل للنشر)."
            echo "NEXORA_ALLOW_DEBUG_SIGNING=1" >> $GITHUB_ENV
          else
            echo "::error::لا يوجد NEXORA_KEYSTORE_BASE64 — نرفض بناء/نشر APK بمفتاح التصحيح: المتاجر (Softonic/Amazon) ترفضه برسالة 'not correctly signed'. أضف السر NEXORA_KEYSTORE_BASE64 ثم أعد التشغيل."
            exit 1
          fi
```

ثم **Commit changes** (أضف في رسالة الالتزام `[skip ci]` حتى لا يعيد البناء ملف APK الحالي وتتغير بصمته).

## الطريقة 2 — بملف الترقيع

ملف `docs/nexora-signing-fix.patch` في هذا المستودع يحتوي التعديلين كاملين.
في استنساخ `nexora-social-2026`:

```bash
git apply docs/nexora-signing-fix.patch
git commit -m "fix(android): fail release build without production keystore [skip ci]"
git push
```

## ماذا يغيّر هذا؟

| قبل | بعد |
|---|---|
| إذا غاب مفتاح الإنتاج → يُوقَّع APK بصمت بمفتاح التصحيح (تنتجه المتاجر: "not correctly signed") | **يفشل البناء برسالة واضحة** تشرح السبب |
| سير العمل يحذّر فقط ثم ينشر | بناءات النشر (push) **تتوقف مبكرًا** إذا غاب السر؛ بناءات PR وحدها تسمح بالتوقيع التجريبي |
| — | للبناء المحلي التجريبي فقط: `NEXORA_ALLOW_DEBUG_SIGNING=1` |

ملاحظة: فحص `scripts/inspect-apk.mjs` (بصمة الشهادة المعتمدة `240e002f…`) يبقى حارسًا
أخيرًا في CI — هذا الإصلاح يجعل الحماية تبدأ من لحظة البناء نفسها.
