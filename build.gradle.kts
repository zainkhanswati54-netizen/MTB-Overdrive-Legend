plugins {
    kotlin("jvm") version "1.9.23" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
}
