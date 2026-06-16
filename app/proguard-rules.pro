# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ==========================================
# H6 — ProGuard / R8 keep rules for release
# ==========================================

# --- Kotlinx Serialization ---
# Keep @Serializable classes and their generated serializers.
# The kotlinx.serialization compiler plugin generates companions and
# serializer classes that R8 would otherwise strip or rename.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep serializers for all @Serializable classes
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep project @Serializable DTOs and their serializers
-keep,includedescriptorclasses class com.example.habittracker.data.remote.dto.**$$serializer { *; }
-keepclassmembers class com.example.habittracker.data.remote.dto.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.habittracker.data.remote.dto.** {
    kotlinx.serialization.KSerializer serializer(...);
}
# Keep the DTO classes themselves (field names needed for JSON keys)
-keepclassmembers class com.example.habittracker.data.remote.dto.HabitDto { *; }
-keepclassmembers class com.example.habittracker.data.remote.dto.HabitLogDto { *; }

# --- Retrofit ---
# Retrofit does reflection on generic parameters. InnerClasses is necessary to
# use Alarm and TypeToken types by default.
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when available.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are
# created with Proxy and). To keep the interfaces and annotated methods, R8 needs
# explicit rules.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep HabitApi interface methods
-keep interface com.example.habittracker.data.remote.HabitApi { *; }

# --- OkHttp ---
-dontwarn okhttp3.**
-dontwarn okio.**

# --- Hilt / Dagger ---
# Hilt generates components and modules; keep entry points.
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.Module class * { *; }

# --- Room ---
# Room uses generated code. Keep entity classes.
-keep class com.example.habittracker.data.local.entity.** { *; }
-keep class com.example.habittracker.data.local.dao.** { *; }

# --- AndroidX / Compose ---
# Keep Lifecycle and ViewModel classes
-keep class androidx.lifecycle.** { *; }

# --- Kotlin ---
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**