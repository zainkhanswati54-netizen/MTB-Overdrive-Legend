plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    api("com.badlogicgames.gdx:gdx:${rootProject.property("gdxVersion")}")
    api("com.badlogicgames.gdx:gdx-box2d:${rootProject.property("gdxVersion")}")
    implementation(kotlin("stdlib"))
}

sourceSets {
    main {
        kotlin.srcDirs("src/main/kotlin")
        resources.srcDirs("../assets")
    }
}
