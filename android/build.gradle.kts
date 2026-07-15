plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.mtbgame.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mtbgame.android"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            kotlin.srcDirs("src/main/kotlin")
            res.srcDirs("src/main/res")
            assets.srcDirs("../assets")
        }
    }

    packaging {
        resources.excludes.add("META-INF/robovm/ios/robovm.xml")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx-backend-android:${rootProject.property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-platform:${rootProject.property("gdxVersion")}:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-platform:${rootProject.property("gdxVersion")}:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-platform:${rootProject.property("gdxVersion")}:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-platform:${rootProject.property("gdxVersion")}:natives-x86_64")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${rootProject.property("gdxVersion")}:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${rootProject.property("gdxVersion")}:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${rootProject.property("gdxVersion")}:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${rootProject.property("gdxVersion")}:natives-x86_64")
}
