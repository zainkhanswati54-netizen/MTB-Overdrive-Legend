plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${rootProject.property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-platform:${rootProject.property("gdxVersion")}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${rootProject.property("gdxVersion")}:natives-desktop")
}

application {
    mainClass.set("com.mtbgame.desktop.DesktopLauncherKt")
}

sourceSets {
    main {
        kotlin.srcDirs("src/main/kotlin")
        resources.srcDirs("../assets")
    }
}
