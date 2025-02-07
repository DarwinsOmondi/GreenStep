plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.greenstep"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.greenstep"
        minSdk = 25
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    // 🚀 Fix for duplicate META-INF files
    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}


dependencies {

    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.firestore)
    dependencies {

        // Core Libraries
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)

        // Compose Libraries
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)

        // Material3
        implementation(libs.androidx.material3)

        // Firebase Authentication
        implementation(libs.firebase.auth)

        // Identity
        implementation(libs.identity.jvm)

        // Espresso Testing
        implementation(libs.androidx.espresso.core)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        // Unit Testing
        testImplementation(libs.junit)

        // Compose Test Libraries
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)

        // Debugging Libraries
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)

        // Navigation
        implementation(libs.androidx.navigation.compose)

        // No need for duplicate or incorrect Material3 dependencies

        // Retrofit
        implementation(libs.retrofit)

        // Gson Converter for Retrofit (used for JSON parsing)
        implementation(libs.converter.gson)

        // OkHttp for making network requests (used internally by Retrofit)
        implementation(libs.okhttp)

        // OkHttp logging interceptor (optional, but useful for debugging)
        implementation(libs.logging.interceptor)

        implementation (libs.mpandroidchart)


        implementation(libs.androidx.material.icons.extended)
    }
}