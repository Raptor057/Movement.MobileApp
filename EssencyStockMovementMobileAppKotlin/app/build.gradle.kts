plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.essency.essencystockmovement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.essency.essencystockmovement"
        minSdk = 24
        targetSdk = 34
        versionCode = 92
        versionName = "beta-1.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


signingConfigs {
    create("release") {
        storeFile = file("essency-release-key.jks")
        storePassword = "Sh4QGvJjajB6feBbfiAi"
        keyAlias = "essency_release"
        keyPassword = "Sh4QGvJjajB6feBbfiAi"
    }
}

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
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
        viewBinding = true
    }

    packaging {
        resources {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/NOTICE.md")
            excludes.add("META-INF/NOTICE.txt")
        }
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
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.androidx.swiperefreshlayout)
    //implementation(libs.androidx.security.crypto.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Dependencias para enviar correos con JavaMail
    dependencies {
        implementation("com.sun.mail:android-mail:1.6.7")
        implementation("com.sun.mail:android-activation:1.6.7")
        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        implementation("androidx.security:security-crypto:1.1.0-alpha06")
    }

}