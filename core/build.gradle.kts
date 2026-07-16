plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
}

dependencies {
    api("com.badlogicgames.gdx:gdx:${property("gdxVersion")}")
    api("com.badlogicgames.gdx:gdx-box2d:${property("gdxVersion")}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
