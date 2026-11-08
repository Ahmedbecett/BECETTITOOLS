# BECETTI TOOLS — ProGuard/R8 rules for the Release build
# يتم تفعيل التصغير (minify) وحذف الموارد غير المستخدمة (shrinkResources) في نوع البناء release.

# --- كود التطبيق entierًا يُحفظ (للتأمل عبر Room/Gson/MLKit وغيرها) ---
-keep class com.becetti.tools.** { *; }
-keepnames class com.becetti.tools.data.local.entity.** { *; }
-keepnames class com.becetti.tools.data.local.dao.** { *; }

# --- Retrofit + Gson (واجهة العملات تُستخدم عبر التNull) ---
-keep class com.becetti.tools.data.remote.CurrencyApi { *; }
-keepattributes Signature, *Annotation*

# --- Room ---
-keep class androidx.room.** { *; }

# --- ML Kit (مربوطة بحزمة التطبيق) ---
-keep class com.google.mlkit.** { *; }

# --- ZXing Core (توليد QR) ---
-keep class com.google.zxing.** { *; }

# --- PDFBox Android ---
-keep class org.apache.pdfbox.** { *; }
-keep class com.tom_roush.pdfbox.** { *; }

# --- المكتبات الأصلية تُحفظ كما هي ---
-keepclasseswithmembernames class * {
    native <methods>;
}

# --- تحذيرات آمنة تُتجاهل (مكتبات rifer إلى فئات غير موجودة في هذه الحزمة) ---
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**
-dontwarn com.tom_roush.pdfbox.**
-dontwarn org.apache.pdfbox.**
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn androidx.**

# --- إزالة سجلات التصحيح في النسخة النهائية ---
-assumenosideeffects class android.util.Log {
    int d(java.lang.String, java.lang.String);
    int v(java.lang.String, java.lang.String);
}
