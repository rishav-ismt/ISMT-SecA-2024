plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
}

android {
    namespace = "np.edu.ismt.rishavchudal.ismt_2024_seca"
    compileSdk = 34

    defaultConfig {
        applicationId = "np.edu.ismt.rishavchudal.ismt_2024_seca"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    //ViewBinding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.room)
    ksp(libs.room.compiler)

    //for location
    implementation(libs.location)

    //for google maps
    implementation(libs.googleMaps)

    //for cameraX
    implementation(libs.cameraXCore)
    implementation(libs.camera2)
    implementation(libs.cameraLifeCycle)
    implementation(libs.cameraVideo)
    implementation(libs.cameraView)
    implementation(libs.cameraExtensions)
}