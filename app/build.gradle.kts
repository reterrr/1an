plugins {
    alias(libs.plugins.android.application)

    id("io.objectbox")

    id("io.freefair.lombok") version "8.4" // modern Lombok plugin
}

android {
    namespace = "com.example.p2p"
    compileSdk = 35

    packaging {
        resources {

            pickFirsts += "META-INF/io.netty.versions.properties"

            excludes += "META-INF/INDEX.LIST"
        }
    }

    defaultConfig {
        applicationId = "com.example.p2p"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.jmdns)
    implementation(libs.objectbox)
    implementation(libs.lombok)
    implementation(libs.signal)
    implementation(libs.bcrypt)

    implementation(libs.jackson.databind)
    implementation(libs.jackson.core)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.datatype.jsr310)

    implementation(libs.nb)
    implementation(libs.nc)
    implementation(libs.nh)
    implementation(libs.nt)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.hilt.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    annotationProcessor(libs.lombok)
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}