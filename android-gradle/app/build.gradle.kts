plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.daffiyanet.addwan"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.daffiyanet.addwan"
        minSdk = 16
        targetSdk = 36
        versionCode = 4
        versionName = "2.3"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
}

