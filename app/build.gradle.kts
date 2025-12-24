plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.codewithdipesh.habitized"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.codewithdipesh.habitized"
        minSdk = 26
        targetSdk = 35
        versionCode = 11
        versionName = "1.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //daggerhilt
    implementation("com.google.dagger:hilt-android:2.56.1")
    kapt("com.google.dagger:hilt-compiler:2.56.1")

    //navigation
    implementation("androidx.navigation:navigation-compose:2.8.9")
    //coroutines
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    //local storage - room db
    val room_version = "2.7.0"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-paging:$room_version")
    androidTestImplementation("androidx.room:room-testing:$room_version")

    //calendar
    implementation("com.kizitonwose.calendar:compose:2.6.2")

    //lottie
    implementation("com.github.LottieFiles:dotlottie-android:0.4.1")

    //coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    //firebase analytics
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics-ndk")

    //widget glance
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")

    //capturable
    implementation("dev.shreyaspatil:capturable:2.1.0")

    //media player
    runtimeOnly("net.protyposis.android.mediaplayer:mediaplayer:4.5.0")

}