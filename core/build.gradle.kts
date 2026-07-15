plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${rootProject.property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-box2d:${rootProject.property("gdxVersion")}")
    implementation(kotlin("stdlib"))
}

sourceSets {
    main {
        kotlin.srcDirs("src/main/kotlin")
        resources.srcDirs("../assets")
    }
}
