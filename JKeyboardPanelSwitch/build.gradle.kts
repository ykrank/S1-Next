
plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "cn.dreamtobe.kpswitch"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = 17
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
}