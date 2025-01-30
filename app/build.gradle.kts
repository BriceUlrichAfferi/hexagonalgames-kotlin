plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
  id("com.google.gms.google-services")
  id("dagger.hilt.android.plugin")
}

android {
  namespace = "com.openclassrooms.hexagonal.games"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.openclassrooms.hexagonal.games"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

    buildTypes {
      debug {
        // Debug specific settings
        isMinifyEnabled = false
        // You can add more debug-specific settings here if needed
      }
      release {
        isMinifyEnabled = false
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        // Release specific settings
      }
    }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.11"
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
}

dependencies {
  // Kotlin
  implementation(platform(libs.kotlin.bom))

  // DI (Hilt)
  implementation(libs.hilt)
  implementation(libs.firebase.appcheck.playintegrity)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.firebase.messaging.ktx)
  implementation(libs.junit.ktx)
  ksp(libs.hilt.compiler)
  implementation(libs.hilt.navigation.compose)

  // Compose
  implementation(platform(libs.compose.bom))
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.material)
  implementation(libs.compose.material3)
  implementation(libs.lifecycle.runtime.compose)
  debugImplementation(libs.compose.ui.tooling)
  debugImplementation(libs.compose.ui.test.manifest)
  implementation(libs.hilt) // Hilt dependency
  ksp(libs.hilt.compiler)  // Hilt compiler for annotation processing
  implementation(libs.hilt.navigation.compose) // Hilt integration for Compose

  // Firebase
  implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
  implementation("com.google.firebase:firebase-analytics")
  implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication KTX for coroutines
  implementation("com.google.firebase:firebase-auth")
  implementation("com.google.firebase:firebase-firestore")
  implementation("com.google.firebase:firebase-storage")
  implementation("com.google.firebase:firebase-appcheck-debug")

  // FirebaseUI
  implementation("com.firebaseui:firebase-ui-auth:8.0.1")

  // Coil (Image loading)
  implementation(libs.coil.compose)

  // Navigation & Permissions
  implementation(libs.activity.compose)
  implementation(libs.navigation.compose)
  implementation("androidx.activity:activity-ktx:1.8.2")
  implementation("androidx.compose.material3:material3:1.4.0-alpha05")
  implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha05")
  implementation("com.google.accompanist:accompanist-permissions:0.37.0")

  // Coroutines
  implementation(libs.kotlinx.coroutines.android)

  // Unit testing
  testImplementation("junit:junit:4.13.2")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
  testImplementation("org.mockito:mockito-core:4.11.0")
  testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
  testImplementation("org.mockito:mockito-inline:4.11.0")
  testImplementation ("com.google.firebase:firebase-firestore:24.0.0 ")
  testImplementation ("org.mockito:mockito-core:4.5.1")
  testImplementation ("androidx.arch.core:core-testing:2.1.0")

// Firebase dependencies for testing
  testImplementation ("com.google.firebase:firebase-auth:21.0.7")
  testImplementation ("io.mockk:mockk:1.12.0")

  // Compose UI testing
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
  androidTestImplementation ("androidx.test.ext:junit:1.1.5")
  androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation ("androidx.arch.core:core-testing:2.1.0")

  // Additional testing dependencies
  testImplementation(libs.junit)
  androidTestImplementation(libs.ext.junit)
  androidTestImplementation(libs.espresso.core)

  // Coroutine support for Firebase tasks
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.5.2")
}
