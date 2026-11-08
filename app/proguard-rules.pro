# BECETTI TOOLS ProGuard / R8 Rules (Amazon Appstore Release Build)

-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, *Annotation*

# Keep application classes, Room entities/DAOs, Retrofit interfaces & data models
-keep class com.becetti.** { *; }

# Room Database
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# Retrofit & Gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-dontwarn retrofit2.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ML Kit & CameraX (preserve JNI entrypoints)
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.odml.** { *; }
-keep @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembers class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**

# PDFBox-Android (keep PDDocument & font/cmap loaders, strip unused BouncyCastle PQC)
-keep class com.tom_roush.pdfbox.** { *; }
-keep class com.tom_roush.fontbox.** { *; }
-keep class com.tom_roush.harmony.** { *; }
-dontwarn com.tom_roush.**
-dontwarn org.bouncycastle.**
