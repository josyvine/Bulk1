import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.util.Base64

plugins {
  alias(libs.plugins.android.application)
}

android {
  namespace = "com.vineyard.bulk.app"
  compileSdk { version = release(36) { minorApiLevel = 1 } }
  ndkVersion = "28.0.12433566"

  defaultConfig {
    applicationId = "com.vineyard.bulk.app"
    minSdk = 26
    targetSdk = 36
    versionCode = 2
    versionName = "1.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    externalNativeBuild {
      cmake {
        cppFlags("-std=c++17", "-fvisibility=hidden", "-O3")
        arguments("-DANDROID_LD=lld", "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
      }
    }

    ndk {
      abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
    }
  }

  signingConfigs {
    create("release") {
      // 1. KEYSTORE BASE64 / PATH RESOLUTION
      val base64Keystore = System.getenv("KEYSTORE_BASE64")
      val keystorePathEnv = System.getenv("KEYSTORE_PATH")

      val resolvedKeystore = when {
        // Option A: If Base64 string is provided in env, decode it directly
        !base64Keystore.isNullOrBlank() -> {
          val decodedFile = file("${rootDir}/safevoice.jks")
          decodedFile.writeBytes(Base64.getDecoder().decode(base64Keystore.trim()))
          decodedFile
        }
        // Option B: KEYSTORE_PATH environment variable passed by YAML
        !keystorePathEnv.isNullOrBlank() -> file(keystorePathEnv)
        // Option C: safevoice.jks decoded in workflow workspace
        file("${rootDir}/safevoice.jks").exists() -> file("${rootDir}/safevoice.jks")
        // Option D: Local default fallback
        else -> file("${rootDir}/my-upload-key.jks")
      }

      storeFile = resolvedKeystore

      // 2. KEYSTORE PASSWORD (matches YAML KEYSTORE_PASSWORD)
      storePassword = System.getenv("KEYSTORE_PASSWORD") ?: System.getenv("STORE_PASSWORD")

      // 3. KEY ALIAS (matches YAML KEY_ALIAS with default fallback "bulk")
      keyAlias = System.getenv("KEY_ALIAS") ?: "bulk"

      // 4. KEY PASSWORD (matches YAML KEY_PASSWORD with fallback to KEYSTORE_PASSWORD)
      keyPassword = System.getenv("KEY_PASSWORD")
        ?: System.getenv("KEYSTORE_PASSWORD")
        ?: System.getenv("STORE_PASSWORD")
    }

    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      isMinifyEnabled = false
      isShrinkResources = false
      isDebuggable = true
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }

  externalNativeBuild {
    cmake {
      path = file("src/main/cpp/CMakeLists.txt")
      version = "3.22.1"
    }
  }

  packaging {
    jniLibs {
      useLegacyPackaging = false
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildFeatures {
    compose = false
    buildConfig = true
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

dependencies {
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.recyclerview:recyclerview:1.4.0")
  implementation("androidx.constraintlayout:constraintlayout:2.2.1")
  implementation(libs.play.services.ads)
}