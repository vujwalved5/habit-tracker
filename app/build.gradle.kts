import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.habittracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.habittracker"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // H3: Read Cloudflare Worker URL from local.properties so it's never in source control
        val localProperties = Properties()
        val localPropsFile = rootProject.file("local.properties")
        if (localPropsFile.exists()) {
            localPropsFile.inputStream().use { localProperties.load(it) }
        }
        val cloudflareBaseUrl = localProperties.getProperty(
            "CLOUDFLARE_BASE_URL",
            "https://your-worker.workers.dev"
        )
        buildConfigField("String", "CLOUDFLARE_BASE_URL", "\"$cloudflareBaseUrl\"")

        // H1: API key for Bearer token auth against the Cloudflare Worker
        val cloudflareApiKey = localProperties.getProperty(
            "CLOUDFLARE_API_KEY",
            ""
        )
        buildConfigField("String", "CLOUDFLARE_API_KEY", "\"$cloudflareApiKey\"")
    }

    buildTypes {
        release {
            // H6: Enable R8 code shrinking + resource shrinking for release
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        compose = true
        // H3: Enable BuildConfig generation so CLOUDFLARE_BASE_URL is available at runtime
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Glance
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Retrofit & OkHttp
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp.logging)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Konfetti
    implementation(libs.konfetti.compose)
    
    // Coil
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
