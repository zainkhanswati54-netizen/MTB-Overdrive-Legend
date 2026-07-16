package com.example.mbx

import com.badlogic.gdx.Game
import com.example.mbx.render.Biome
import com.example.mbx.screens.ContinueScreen
import com.example.mbx.screens.GameplayScreen
import com.example.mbx.screens.MainMenuScreen
import com.example.mbx.screens.MapSelectionScreen
import com.example.mbx.screens.ShopScreen

/**
 * Shared entry point. Android's AndroidLauncher and Desktop's DesktopLauncher
 * both just construct this class and hand it to their platform application.
 *
 * Screen flow:
 *   Continue -> Main Menu -> (Tap to start) Gameplay
 *                         -> (gear icon)    Map Selection -> back to Main Menu
 *                         -> (Shop)         Shop -> back to Main Menu
 *   Gameplay -> (pause > Menu) Main Menu
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

    fun goToMapSelection() = setScreen(MapSelectionScreen(this))
    fun goToContinueScreen() = setScreen(ContinueScreen(this))
    fun goToMainMenu() = setScreen(MainMenuScreen(this))
    fun goToShop() = setScreen(ShopScreen(this))
    fun goToGameplay() = setScreen(GameplayScreen(this, selectedBiome))

    override fun dispose() {
        super.dispose()
        assets.dispose()
    }
}
