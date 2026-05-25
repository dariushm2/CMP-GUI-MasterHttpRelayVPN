# Ignore unresolved references in OkHttp (GraalVM, Conscrypt, BouncyCastle, OpenJSSE)
# These are optional platform-specific features that are never called on standard desktop JVM environments.
-dontwarn okhttp3.internal.graal.**
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn org.graalvm.**
-dontwarn com.oracle.svm.**

# Ignore warnings from Ktor / coroutines if any arise
-dontwarn io.ktor.**
-dontwarn kotlinx.coroutines.**

# Suppress verbose JVM note logs to keep build output clean
-dontnote **

# =========================================================================
# Obfuscation Safety Rules
# =========================================================================

# Keep our own classes and members to prevent any DI (Koin) or Serialization issues
-keep class com.darius.lionvpn.** { *; }

# Keep Koin DI core annotation definitions and reflection targets
-keep class org.insertkoin.** { *; }
-keep class org.koin.** { *; }

# Keep Kotlin Serialization metadata and generated serializers
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable *** Serializer(...);
}
-keep class kotlinx.serialization.** { *; }

# Keep Ktor engine and networking classes
-keep class io.ktor.** { *; }

