
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
}