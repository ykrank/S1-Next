import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.gradleVersionsPlugin)
    alias(libs.plugins.androidKsp)
    kotlin("kapt")
    id("kotlin-parcelize")
}

val properties = gradleLocalProperties(rootDir, providers)
val mStoreFile: String? = properties.getProperty("storeFile")
val mStorePassword: String? = properties.getProperty("storePassword")
val mKeyAlias: String? = properties.getProperty("keyAlias")
val mKeyPassword: String? = properties.getProperty("keyPassword")
val httpDnsId = properties.getProperty("httpDnsId") ?: "\"\""
val httpDnsSecret = properties.getProperty("httpDnsSecret") ?: "\"\""

val appVersionCode = 91
val appVersionName = "3.1"
val appVersionSuffix = ""

android {
    namespace = "me.ykrank.s1next"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "me.ykrank.s1next"
        minSdk = 26
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = "${appVersionName}.${appVersionCode}${appVersionSuffix}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        if (!mStoreFile.isNullOrEmpty()) {
            create("release") {
                keyAlias = mKeyAlias
                keyPassword = mKeyPassword
                storeFile = file(mStoreFile)
                storePassword = mStorePassword
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
        }
    }
    buildTypes {
        debug {
            multiDexEnabled = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            if (signingConfigs.findByName("release") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }

        create("alpha") {
            multiDexEnabled = true
            applicationIdSuffix = ".alpha"
            versionNameSuffix = "-alpha"
            if (signingConfigs.findByName("release") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            matchingFallbacks += listOf("release", "debug")
        }

        release {
            multiDexEnabled = true
            if (signingConfigs.findByName("release") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    buildTypes.forEach {
        it.buildConfigField("String", "DB_NAME", "\"s1.db\"")
        it.buildConfigField("String", "HTTP_DNS_ID", httpDnsId)
        it.buildConfigField("String", "HTTP_DNS_SECRET", httpDnsSecret)
    }

    flavorDimensions += "market"
    productFlavors {
        create("play") {
            dimension = "market"
            manifestPlaceholders["APP_CHANNEL"] = "play.google.com"
            versionNameSuffix = "-play"
        }
        create("normal") {
            dimension = "market"
            manifestPlaceholders["APP_CHANNEL"] = "normal"
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

val alphaImplementation by configurations
dependencies {
    implementation(fileTree("libs") { include("*.jar", "*.aar") })

    implementation(project(":library"))
    implementation(project(":JKeyboardPanelSwitch"))

    kapt(libs.databinding.compiler)
    implementation(libs.paging)

    implementation(libs.bugly.nativecrashreport)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    implementation(libs.androidx.transition)

    implementation(libs.okhttp.urlconnection)
    implementation(libs.okhttp.coroutines)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.adapter.rxjava2)
    implementation(libs.retrofit2.converter.jackson)
    implementation(libs.retrofit2.converter.scalars)

    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.databind)

    implementation(libs.paperparcel)
    implementation(libs.paperparcel.kotlin) // Optional
    implementation(libs.paperparcel.api)
    kapt(libs.paperparcel.compiler)

    ksp(libs.glide.ksp)

    implementation(libs.photoview)
    implementation(libs.quicksidebar)

//  flipper
    releaseImplementation(libs.flipper.noop)
    alphaImplementation(libs.flipper.noop)
    debugImplementation(libs.flipper)
    debugImplementation(libs.soloader)
    debugImplementation(libs.flipper.network.plugin)

    implementation(libs.alicloud.android.httpdns)

    //room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
}
