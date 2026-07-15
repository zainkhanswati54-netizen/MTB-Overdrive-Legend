package com.mtbgame

import com.badlogic.gdx.Game
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Gdx
import com.mtbgame.screens.MenuScreen

/**
 * Root Game class. Yeh sirf ek "screen switcher" hai.
 * MenuScreen -> GameScreen -> wapas MenuScreen (crash ya finish ke baad)
 */
class MtbGame : Game() {

    lateinit var prefs: Preferences

    override fun create() {
        prefs = Gdx.app.getPreferences("mtb_save")
        setScreen(MenuScreen(this))
    }
}
