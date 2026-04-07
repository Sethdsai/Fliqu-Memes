# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keep class com.fliqu.memes.model.** { *; }
