package com.example.mbx

import com.badlogic.gdx.Game
import com.example.mbx.render.Biome
import com.example.mbx.screens.ContinueScreen
import com.example.mbx.screens.MapSelectionScreen

/**
 * Shared entry point. Android's AndroidLauncher and Desktop's DesktopLauncher
 * both just construct this class and hand it to their platform application.
 */
class MountainBikeGame : Game() {

    lateinit var assets: Assets
        private set

    // Remembers the last biome picked on the selection screen.
    var selectedBiome: Biome = Biome.FOREST

    override fun create() {
        assets = Assets()
        setScreen(ContinueScreen(this))
    }

    fun goToMapSelection() {
        setScreen(MapSelectionScreen(this))
    }

    fun goToContinueScreen() {
        setScreen(ContinueScreen(this))
    }

    override fun dispose() {
        super.dispose()
        assets.dispose()
    }
}
