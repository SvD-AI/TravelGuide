plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)

}

android {
    namespace = "com.example.travelguide"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.travelguide"
        minSdk = 23
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
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.lottie)
    implementation(libs.osmdroid.android)
    implementation(libs.play.services.maps)
    implementation(libs.jts.core)
    implementation(libs.android.maps.utils)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.places)
    implementation(libs.play.services.places)
    implementation(libs.androidx.runner)
    implementation(libs.okhttp)
    implementation(libs.google.maps.services)
    implementation(libs.play.services.location)
    implementation(libs.volley)
    implementation(libs.glide)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    kapt(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
}