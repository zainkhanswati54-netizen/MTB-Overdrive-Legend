plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-box2d:${property("gdxVersion")}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
