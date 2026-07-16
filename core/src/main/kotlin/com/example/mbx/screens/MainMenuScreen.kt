package com.example.mbx.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.example.mbx.MountainBikeGame
import com.example.mbx.render.IconRenderer
import com.example.mbx.render.ParallaxRenderer

private const val WORLD_W = 1200f
private const val WORLD_H = 675f

class MainMenuScreen(private val game: MountainBikeGame) : Screen {

    private val camera = OrthographicCamera()
    private val viewport: Viewport = FitViewport(WORLD_W, WORLD_H, camera)
    private val touchPoint = Vector3()

    private val white = Color(0.949f, 0.969f, 0.965f, 1f)
    private val gold = Color(0.910f, 0.769f, 0.408f, 1f)

    private val settingsRegion = TouchRegion(WORLD_W - 60f, WORLD_H - 60f, 44f)
    private val shopRegion = TouchRegionRect(WORLD_W - 260f, 110f, 220f, 64f)

    override fun show() {}

    override fun render(delta: Float) {
        handleInput()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        val sr = game.assets.shapeRenderer
        sr.projectionMatrix = camera.combined

        ParallaxRenderer.render(sr, game.selectedBiome, 0f, 0f, WORLD_W, WORLD_H, seed = 555L)

        // Rider standing near the left, matching the reference screenshot's calm "menu" pose
        IconRenderer.drawBike(sr, WORLD_W * 0.22f, WORLD_H * 0.42f, 120f, white)

        // Settings gear (top-right) -> environment/map selection
        IconRenderer.drawSettingsGear(sr, settingsRegion.x, settingsRegion.y, 40f, white)

        // Shop button
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.setColor(Color(gold).apply { a = 0.85f })
        sr.rect(shopRegion.x, shopRegion.y, shopRegion.w, shopRegion.h)
        sr.end()

        val prefs = Gdx.app.getPreferences("mbx_prefs")
        val best = prefs.getFloat("best_distance_m", 0f)
        val total = prefs.getFloat("total_distance_m", 0f)

        val batch = game.assets.batch
        val layout = game.assets.layout
        batch.projectionMatrix = camera.combined
        batch.begin()

        game.assets.labelFont.color = white
        layout.setText(game.assets.labelFont, "Best Distance: ${best.toInt()}m")
        game.assets.labelFont.draw(batch, layout, 24f, WORLD_H - 24f)

        val totalLabel = if (total >= 1000f) String.format("%.1f km", total / 1000f) else "${total.toInt()} m"
        layout.setText(game.assets.labelFont, "Total Distance: $totalLabel")
        game.assets.labelFont.draw(batch, layout, 24f, WORLD_H - 60f)

        game.assets.titleFont.color = white
        layout.setText(game.assets.titleFont, "Tap to start")
        game.assets.titleFont.draw(batch, layout, WORLD_W / 2f - layout.width / 2f, 110f)

        game.assets.bodyFont.color = Color(0.055f, 0.231f, 0.212f, 1f)
        layout.setText(game.assets.bodyFont, "Shop")
        game.assets.bodyFont.draw(batch, layout, shopRegion.x + shopRegion.w / 2f - layout.width / 2f, shopRegion.y + shopRegion.h / 2f + 8f)

        batch.end()
    }

    private fun handleInput() {
        if (!Gdx.input.justTouched()) return
        touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touchPoint)

        when {
            settingsRegion.contains(touchPoint.x, touchPoint.y) -> game.goToMapSelection()
            shopRegion.contains(touchPoint.x, touchPoint.y) -> game.goToShop()
            else -> game.goToGameplay()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {}

    private data class TouchRegion(val x: Float, val y: Float, val radius: Float) {
        fun contains(px: Float, py: Float): Boolean {
            val dx = px - x
            val dy = py - y
            return dx * dx + dy * dy <= radius * radius
        }
    }

    private data class TouchRegionRect(val x: Float, val y: Float, val w: Float, val h: Float) {
        fun contains(px: Float, py: Float) = px in x..(x + w) && py in y..(y + h)
    }
}
