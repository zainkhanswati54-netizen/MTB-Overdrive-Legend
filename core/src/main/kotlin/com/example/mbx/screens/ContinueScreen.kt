package com.example.mbx.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.example.mbx.MountainBikeGame
import com.example.mbx.render.IconRenderer

private const val WORLD_W = 1200f
private const val WORLD_H = 675f

class ContinueScreen(private val game: MountainBikeGame) : Screen {

    private val camera = OrthographicCamera()
    private val viewport: Viewport = FitViewport(WORLD_W, WORLD_H, camera)
    private val touchPoint = Vector3()

    private val bgTop = Color(0.043f, 0.180f, 0.200f, 1f)
    private val bgBottom = Color(0.055f, 0.231f, 0.212f, 1f)
    private val white = Color(0.949f, 0.969f, 0.965f, 1f)

    private var elapsed = 0f

    // Tap regions (in world units), computed relative to WORLD_W/H
    private val exitRegion = TouchRegion(60f, WORLD_H - 60f, 70f)
    private val settingsRegion = TouchRegion(WORLD_W - 60f, WORLD_H - 60f, 70f)

    override fun show() {}

    override fun render(delta: Float) {
        elapsed += delta
        handleInput()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        val sr = game.assets.shapeRenderer
        sr.projectionMatrix = camera.combined

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.rect(0f, 0f, WORLD_W, WORLD_H, bgBottom, bgBottom, bgTop, bgTop)
        sr.end()

        // Bike icon, centered slightly above middle
        IconRenderer.drawBike(sr, WORLD_W / 2f, WORLD_H / 2f + 60f, 160f, white)

        // Corner icons
        IconRenderer.drawExitIcon(sr, exitRegion.x, exitRegion.y, 40f, Color(white).apply { a = 0.85f })
        IconRenderer.drawSettingsGear(sr, settingsRegion.x, settingsRegion.y, 40f, Color(white).apply { a = 0.85f })

        // Text
        val batch = game.assets.batch
        val layout = game.assets.layout
        batch.projectionMatrix = camera.combined
        batch.begin()

        game.assets.titleFont.color = white
        layout.setText(game.assets.titleFont, "MOUNTAIN BIKE XTREME")
        game.assets.titleFont.draw(batch, layout, WORLD_W / 2f - layout.width / 2f, WORLD_H / 2f - 40f)

        val pulse = 0.35f + 0.65f * (0.5f + 0.5f * MathUtils.sin(elapsed * 3.2f))
        game.assets.bodyFont.color = Color(white).apply { a = pulse }
        layout.setText(game.assets.bodyFont, "Tap to continue")
        game.assets.bodyFont.draw(batch, layout, WORLD_W / 2f - layout.width / 2f, WORLD_H / 2f - 90f)

        batch.end()
    }

    private fun handleInput() {
        if (!Gdx.input.justTouched()) return
        touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touchPoint)

        when {
            exitRegion.contains(touchPoint.x, touchPoint.y) -> {
                Gdx.app.exit()
            }
            settingsRegion.contains(touchPoint.x, touchPoint.y) -> {
                // Settings screen not built yet — placeholder for future work
            }
            else -> {
                game.goToMapSelection()
            }
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
}
