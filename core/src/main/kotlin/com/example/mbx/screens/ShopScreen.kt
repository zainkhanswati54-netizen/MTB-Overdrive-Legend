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

private const val WORLD_W = 1200f
private const val WORLD_H = 675f

class ShopScreen(private val game: MountainBikeGame) : Screen {

    private val camera = OrthographicCamera()
    private val viewport: Viewport = FitViewport(WORLD_W, WORLD_H, camera)
    private val touchPoint = Vector3()

    private val white = Color(0.949f, 0.969f, 0.965f, 1f)
    private val bgTop = Color(0.043f, 0.180f, 0.200f, 1f)
    private val bgBottom = Color(0.055f, 0.231f, 0.212f, 1f)

    private val backRegion = TouchRegion(60f, WORLD_H - 60f, 60f)

    private val swatches = listOf(
        Color(0.949f, 0.969f, 0.965f, 1f),
        Color(0.910f, 0.769f, 0.408f, 1f),
        Color(0.831f, 0.294f, 0.294f, 1f),
        Color(0.298f, 0.588f, 0.902f, 1f)
    )
    private val swatchNames = listOf("Classic White", "Trail Gold", "Crash Red", "Sky Blue")

    override fun show() {}

    override fun render(delta: Float) {
        handleInput()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        val sr = game.assets.shapeRenderer
        sr.projectionMatrix = camera.combined

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.rect(0f, 0f, WORLD_W, WORLD_H, bgBottom, bgBottom, bgTop, bgTop)
        sr.end()

        IconRenderer.drawBackArrow(sr, backRegion.x, backRegion.y, 40f, Color(white).apply { a = 0.9f })

        val startX = WORLD_W / 2f - (swatches.size * 140f) / 2f
        for (i in swatches.indices) {
            val cx = startX + i * 140f + 70f
            val cy = WORLD_H / 2f + 40f
            sr.begin(ShapeRenderer.ShapeType.Filled)
            sr.setColor(swatches[i])
            sr.circle(cx, cy, 45f, 30)
            sr.end()
            IconRenderer.drawBike(sr, cx, cy, 70f, Color(0.055f, 0.231f, 0.212f, 1f))
        }

        val batch = game.assets.batch
        val layout = game.assets.layout
        batch.projectionMatrix = camera.combined
        batch.begin()

        game.assets.labelFont.color = white
        layout.setText(game.assets.labelFont, "Bike Colors")
        game.assets.labelFont.draw(batch, layout, WORLD_W / 2f - layout.width / 2f, WORLD_H - 40f)

        for (i in swatches.indices) {
            val cx = startX + i * 140f + 70f
            layout.setText(game.assets.bodyFont, swatchNames[i])
            game.assets.bodyFont.color = white
            game.assets.bodyFont.draw(batch, layout, cx - layout.width / 2f, WORLD_H / 2f - 40f)
        }

        layout.setText(game.assets.bodyFont, "More gear coming soon — tap a color to preview, back arrow to return")
        game.assets.bodyFont.color = Color(white).apply { a = 0.75f }
        game.assets.bodyFont.draw(batch, layout, WORLD_W / 2f - layout.width / 2f, 80f)

        batch.end()
    }

    private fun handleInput() {
        if (!Gdx.input.justTouched()) return
        touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touchPoint)

        if (backRegion.contains(touchPoint.x, touchPoint.y)) {
            game.goToMainMenu()
        }
        // Color swatches are cosmetic-preview only for now — wiring an actual
        // "equipped color" into BikePhysicsWorld rendering is a natural next step.
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
