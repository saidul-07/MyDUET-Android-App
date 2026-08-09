plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myduet"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.myduet"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.swiperefreshlayout)
    implementation(libs.jsoup)
    implementation("androidx.datastore:datastore-preferences:1.1.3")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}