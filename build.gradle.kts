
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}

buildscript {
//    ext {
//        val userHome = System.properties["user.home"]
//    }

//    repositories {
//        maven { url = uri("https://raw.githubusercontent.com/martinloren/AabResGuard/mvn-repo") }
//        maven {
//            url = userHome + '/.m2/repository'
//        }
//        maven { url = uri("http://maven.aliyun.com/nexus/content/groups/public/" ) }
//    }
//    dependencies {
//        classpath("com.bytedance.android:aabresguard-plugin:0.1.10")
//    }
}


