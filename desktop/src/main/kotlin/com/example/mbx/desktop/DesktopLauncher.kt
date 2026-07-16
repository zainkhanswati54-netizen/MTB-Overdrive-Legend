package com.example.mbx.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.example.mbx.MountainBikeGame

fun main() {
    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("Mountain Bike Xtreme")
        setWindowedMode(1200, 675)
        useVsync(true)
        setForegroundFPS(60)
    }
    Lwjgl3Application(MountainBikeGame(), config)
}
