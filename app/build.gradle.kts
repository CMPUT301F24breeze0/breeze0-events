plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.breeze0events"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.breeze0events"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
}
dependencies {
//    implementation(files("/C:/Users/Qingyun/AppData/Local/Android/Sdk/platforms/android-34/android.jar"))
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.mlkit:barcode-scanning:17.0.3")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.database)
    implementation(libs.espresso.intents)
    implementation(libs.idling.concurrent)
    implementation(libs.uiautomator)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.core)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.google.zxing:core:3.3.3")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0") // for using in Android
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:4.3.1")
    testImplementation ("org.mockito:mockito-inline:4.3.1")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5") // For AndroidJUnit4
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1") // For Espresso (optional, useful for UI tests)
    implementation ("com.google.android.gms:play-services-maps:18.0.0'")
    implementation ("org.osmdroid:osmdroid-android:6.1.10")
}
