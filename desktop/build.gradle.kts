plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

dependencies {
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-platform:${property("gdxVersion")}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${property("gdxVersion")}:natives-desktop")
}

application {
    mainClass.set("com.example.mbx.desktop.DesktopLauncherKt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
