plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.mbx.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mbx"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("assets")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packaging {
        resources.excludes.add("META-INF/robovm/ios/robovm.xml")
    }
}

dependencies {
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx:${property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-box2d:${property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-backend-android:${property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-platform:${property("gdxVersion")}:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-platform:${property("gdxVersion")}:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-platform:${property("gdxVersion")}:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-platform:${property("gdxVersion")}:natives-x86_64")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${property("gdxVersion")}:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${property("gdxVersion")}:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${property("gdxVersion")}:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${property("gdxVersion")}:natives-x86_64")
}
