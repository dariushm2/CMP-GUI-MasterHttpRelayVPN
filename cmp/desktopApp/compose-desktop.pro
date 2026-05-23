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
