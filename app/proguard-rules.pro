# ===================================================================
# 1. Log Stripping, Package Flattening & Metadata Obfuscation (Txtify2 Style)
# ===================================================================

# Strip all Android Logcat calls (d, v, i, w, e, wtf, println) from release bytecode
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
    public static *** wtf(...);
    public static *** println(...);
}

# Flatten internal classes into package 'o' and hide real source metadata
-repackageclasses 'o'
-allowaccessmodification
-renamesourcefileattribute 'SourceFile'

# Preserve only required annotations and reflection signatures
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# ===================================================================
# 2. Native JNI Bridge & App Components
# ===================================================================

# Keep NativeBridge intact for C++ JNI method resolution
-keep class com.vineyard.bulk.app.utils.NativeBridge {
    *;
}

# Preserve standard Android entry components
-keep class * extends android.app.Application
-keep class * extends android.app.Activity
-keep class * extends androidx.fragment.app.Fragment

# Keep custom views and ViewHolders for safe XML inflation
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends androidx.recyclerview.widget.RecyclerView$ViewHolder {
    public <init>(...);
}

# ===================================================================
# 3. Existing App Dependencies (WorkManager, Room, Startup, Ads)
# ===================================================================

# Keep WorkManager and its internal classes
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# Keep Room classes and generated database implementations
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.**

# Keep Android App Startup classes
-keep class androidx.startup.** { *; }
-dontwarn androidx.startup.**

# Keep Google Play Services Ads classes
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**