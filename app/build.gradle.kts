plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.evms"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.evms"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.firebase:firebase-storage")

    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))

    implementation("com.google.firebase:firebase-analytics")

    implementation ("com.google.firebase:firebase-firestore:24.10.0")

    implementation ("com.github.esafirm:android-image-picker:2.4.5")

    implementation ("com.google.firebase:firebase-database:20.0.3") // Use the latest version available

    implementation ("com.google.android.gms:play-services-vision:19.0.0")

    implementation ("com.github.yalantis:ucrop:2.2.8-native")

    implementation ("androidx.appcompat:appcompat:1.4.1")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

}