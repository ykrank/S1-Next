
plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "cn.dreamtobe.kpswitch"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = 17
        consumerProguardFile("proguard-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
}