plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.chocolate.nigerialoanapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.chocolate.nigerialoanapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("boolean", "IS_AAB_BUILD", "true")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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

    implementation(libs.appcompat)
    implementation(libs.utilcodex)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.glide)
    implementation(libs.glide.compiler)
    implementation(libs.fastjson)
    implementation(libs.okgo)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.integrity)
    implementation(libs.gson)
    implementation(libs.eventbus)
    implementation(libs.android.pickerview)
    implementation(libs.installreferrer) {

    }
    implementation(libs.refresh.layout)
    implementation(libs.refresh.footer)
    implementation(libs.refresh.header)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}