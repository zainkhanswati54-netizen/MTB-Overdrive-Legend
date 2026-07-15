package com.mtbgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mtbgame.MtbGame

/**
 * Menu screen: title + best distance + "tap/click to start".
 */
class MenuScreen(private val game: MtbGame) : Screen {

    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val camera = OrthographicCamera()

    override fun show() {
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.53f, 0.86f, 0.92f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = camera.combined
        batch.begin()
        val best = game.prefs.getFloat("best_distance", 0f)
        font.draw(batch, "MOUNTAIN BIKE (Prototype)", 40f, Gdx.graphics.height - 60f)
        font.draw(batch, "Best Distance: ${best.toInt()}m", 40f, Gdx.graphics.height - 100f)
        font.draw(batch, "Click / Tap to Start", 40f, Gdx.graphics.height - 140f)
        batch.end()

        if (Gdx.input.justTouched()) {
            game.setScreen(GameScreen(game))
            dispose()
        }
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
