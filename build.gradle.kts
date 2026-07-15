plugins {
    kotlin("jvm") version "1.9.23" apply false
    kotlin("android") version "1.9.23" apply false
    id("com.android.application") version "8.5.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
}
