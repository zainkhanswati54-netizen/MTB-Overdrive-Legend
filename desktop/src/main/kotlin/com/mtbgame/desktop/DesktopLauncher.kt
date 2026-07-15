package com.mtbgame.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.mtbgame.MtbGame

fun main() {
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("Mountain Bike Prototype")
    config.setWindowedMode(1280, 720)
    config.useVsync(true)
    Lwjgl3Application(MtbGame(), config)
}
