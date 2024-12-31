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
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
  //kotlin
  implementation(platform(libs.kotlin.bom))

  //DI
  implementation(libs.hilt)
  ksp(libs.hilt.compiler)
  implementation(libs.hilt.navigation.compose)

  //compose
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

  implementation ("com.google.firebase:firebase-messaging")

  implementation(libs.activity.compose)
  implementation(libs.navigation.compose)
  
  implementation(libs.kotlinx.coroutines.android)
  
  implementation(libs.coil.compose)
  implementation(libs.accompanist.permissions)

  implementation ("androidx.activity:activity-compose:1.5.0")
  // Other dependencies
  implementation("androidx.activity:activity-ktx:1.8.2")

  //Material dependencies for Icons and others

  implementation("androidx.compose.material3:material3:1.4.0-alpha05")
  implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha05")

  //FIREBASE
  implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
  implementation("com.google.firebase:firebase-analytics")
  implementation("com.firebaseui:firebase-ui-auth:8.0.1")
  implementation ("com.google.firebase:firebase-firestore")
  implementation ("com.google.firebase:firebase-storage")



  implementation ("com.google.accompanist:accompanist-permissions:0.37.0")



  testImplementation(libs.junit)
  androidTestImplementation(libs.ext.junit)
  androidTestImplementation(libs.espresso.core)
}