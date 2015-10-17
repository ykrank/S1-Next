# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Android Support Library
# see https://code.google.com/p/android/issues/detail?id=190250
-keep public class android.support.v7.widget.LinearLayoutManager { *; }
# see https://code.google.com/p/android/issues/detail?id=183261
-keep public class android.support.v7.preference.Preference { public <init>(...); }
-keep public class * extends android.support.v7.preference.Preference { public <init>(...); }

# Guava
# https://github.com/google/guava/wiki/UsingProGuardWithGuava
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.collect.MinMaxPriorityQueue

# Jackson databind
-keep public class cl.monsoon.s1next.data.api.model.** { *; }

-dontwarn com.fasterxml.jackson.databind.ext.DOMSerializer
-keepnames class com.fasterxml.jackson.** { *; }

# Okio
# https://github.com/square/okio/issues/144
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Glide
# https://github.com/bumptech/glide#user-content-proguard
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Gradle Retrolambda Plugin
# https://github.com/evant/gradle-retrolambda#user-content-proguard
-dontwarn java.lang.invoke.*

# Retrofit
-keepattributes Signature
-dontwarn retrofit.Platform$Java8

# RxJava
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}
