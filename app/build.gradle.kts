import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.gradleVersionsPlugin)
    alias(libs.plugins.androidKsp)
    kotlin("kapt")
}

val properties = gradleLocalProperties(rootDir, providers)
val _storeFile = properties.getProperty("storeFile")
val _storePassword = properties.getProperty("storePassword")
val _keyAlias = properties.getProperty("keyAlias")
val _keyPassword = properties.getProperty("keyPassword")
val httpDnsId = properties.getProperty("httpDnsId") ?: "\"\""
val httpDnsSecret = properties.getProperty("httpDnsSecret") ?: "\"\""

val appVersionCode = 81
val appVersionName = "3.0.0"

android {
    namespace = "me.ykrank.s1next"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "me.ykrank.s1next"
        minSdk = 23
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = "${appVersionName}.${appVersionCode}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        if (_storeFile != null && !_storeFile.isEmpty()) {
            create("release") {
                keyAlias = _keyAlias
                keyPassword = _keyPassword
                storeFile = file(_storeFile)
                storePassword = _storePassword
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
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.adapter.rxjava2)
    implementation(libs.retrofit2.converter.jackson)
    implementation(libs.retrofit2.converter.scalars)

    implementation(libs.rxcache.runtime)
    implementation(libs.jolyglot.jackson)

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

    implementation(libs.okdownload)
    implementation(libs.okdownload.okhttp)
    implementation(libs.sqlite)

    implementation(libs.alicloud.android.httpdns)

    //room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
}
