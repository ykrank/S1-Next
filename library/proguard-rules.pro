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

# 将.class信息中的类名重新定义为"SourceFile"字符串
-renamesourcefileattribute SourceFile
# 并保留源文件名为"Proguard"字符串，而非原始的类名 并保留行号 // blog from sodino.com
-keepattributes SourceFile,LineNumberTable

# Guava
# https://github.com/google/guava/wiki/UsingProGuardWithGuava
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.collect.MinMaxPriorityQueue
-dontwarn android.databinding.tool.util.**
-dontwarn java.lang.ClassValue

#PaperParcel
-keepclassmembers class **.PaperParcel* {
  static void writeToParcel(...);
}
-keepnames class **.PaperParcel*
-keepnames @paperparcel.PaperParcel class *

# Dagger
-dontwarn dagger.**

# Jackson Model
-keep public class com.github.ykrank.androidtools.widget.uploadimg.SmmsImageDelete { *; }
-keep public class com.github.ykrank.androidtools.widget.uploadimg.SmmsImageUpload** { *; }

#Jackson
-dontwarn com.fasterxml.jackson.databind.ext.**
-keepnames class com.fasterxml.jackson.** { *; }
-keep class org.codehaus.* { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
    public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *;
}
-keep public class * extends com.fasterxml.jackson.databind.JsonDeserializer
-keep public class * extends com.fasterxml.jackson.databind.JsonSerializer


# Glide 4.11
# https://github.com/bumptech/glide#user-content-proguard
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-keepclassmembernames class * {
    @retrofit2.http.* <methods>;
}

# RxJava
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

#jsoup
-keeppackagenames org.jsoup.nodes

#TalkingData
-dontwarn com.tendcloud.tenddata.**
-keep class com.tendcloud.** {*;}
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
    public void *(***);
}
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}
-keep class dice.** {*; }
-dontwarn dice.**

#Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}

#kotlin
-dontwarn kotlin.**
#get rid of null checks at runtime
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

-dontwarn com.bun.miitmdid.interfaces.IIdentifierListener
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings