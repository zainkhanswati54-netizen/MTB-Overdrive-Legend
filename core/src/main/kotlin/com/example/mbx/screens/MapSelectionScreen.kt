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
import com.example.mbx.render.Biome
import com.example.mbx.render.IconRenderer
import com.example.mbx.render.ParallaxRenderer
import kotlin.math.abs
import kotlin.math.round

private const val WORLD_W = 1200f
private const val WORLD_H = 675f
private const val CARD_W = 340f
private const val CARD_H = 380f
private const val CARD_SPACING = 380f

class MapSelectionScreen(private val game: MountainBikeGame) : Screen {

    private val camera = OrthographicCamera()
    private val viewport: Viewport = FitViewport(WORLD_W, WORLD_H, camera)
    private val touchPoint = Vector3()

    private val bgTop = Color(0.043f, 0.180f, 0.200f, 1f)
    private val bgBottom = Color(0.055f, 0.231f, 0.212f, 1f)
    private val white = Color(0.949f, 0.969f, 0.965f, 1f)
    private val gold = Color(0.910f, 0.769f, 0.408f, 1f)

    private val environments = Biome.entries.toList()
    private var currentIndex = environments.indexOf(game.selectedBiome).coerceAtLeast(0)
    private var scrollX = currentIndex * CARD_SPACING
    private var dragging = false
    private var wasTouchedLastFrame = false
    private var suppressDragThisTouch = false

    private val backRegion = TouchRegion(60f, WORLD_H - 60f, 60f)
    private val selectRegion = TouchRegionRect(WORLD_W / 2f - 90f, 40f, 180f, 60f)

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

        // Draw cards left-to-right; only draw ones roughly on-screen
        for (i in environments.indices) {
            val centerX = WORLD_W / 2f - scrollX + i * CARD_SPACING
            if (centerX < -CARD_W || centerX > WORLD_W + CARD_W) continue

            val distFromCenter = abs(centerX - WORLD_W / 2f)
            val scale = (1f - (distFromCenter / CARD_SPACING) * 0.18f).coerceIn(0.75f, 1f)
            val w = CARD_W * scale
            val h = CARD_H * scale
            val cardX = centerX - w / 2f
            val cardY = WORLD_H / 2f - h / 2f + 20f

            val isSelected = i == currentIndex

            ParallaxRenderer.render(sr, environments[i], cardX, cardY, w, h, seed = i.toLong() * 97L)

            // Border
            sr.begin(ShapeRenderer.ShapeType.Line)
            sr.setColor(if (isSelected) gold else Color(white).apply { a = 0.7f })
            val lineWidthPasses = if (isSelected) 3 else 1
            repeat(lineWidthPasses) { pass ->
                sr.rect(cardX - pass, cardY - pass, w + pass * 2, h + pass * 2)
            }
            sr.end()
        }

        // Back arrow
        IconRenderer.drawBackArrow(sr, backRegion.x, backRegion.y, 40f, Color(white).apply { a = 0.9f })

        // Select button
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.setColor(Color(gold).apply { a = 0.9f })
        sr.rect(selectRegion.x, selectRegion.y, selectRegion.w, selectRegion.h)
        sr.end()
        IconRenderer.drawCheck(sr, selectRegion.x + 30f, selectRegion.y + selectRegion.h / 2f, 30f, Color(0.055f, 0.231f, 0.212f, 1f))

        // Text
        val batch = game.assets.batch
        val layout = game.assets.layout
        batch.projectionMatrix = camera.combined
        batch.begin()

        game.assets.labelFont.color = white
        layout.setText(game.assets.labelFont, "Select Environment")
        game.assets.labelFont.draw(batch, layout, WORLD_W / 2f - layout.width / 2f, WORLD_H - 30f)

        layout.setText(game.assets.labelFont, environments[currentIndex].label)
        game.assets.labelFont.draw(batch, layout, WORLD_W / 2f - layout.width / 2f, WORLD_H / 2f - CARD_H / 2f)

        game.assets.bodyFont.color = Color(0.055f, 0.231f, 0.212f, 1f)
        layout.setText(game.assets.bodyFont, "Select")
        game.assets.bodyFont.draw(batch, layout, selectRegion.x + 65f, selectRegion.y + selectRegion.h / 2f + 8f)

        batch.end()
    }

    private fun handleInput() {
        val isTouched = Gdx.input.isTouched
        if (isTouched && !wasTouchedLastFrame) {
            // Touch just started — check for button taps first
            touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touchPoint)

            suppressDragThisTouch = when {
                backRegion.contains(touchPoint.x, touchPoint.y) -> {
                    game.goToMainMenu()
                    true
                }
                selectRegion.contains(touchPoint.x, touchPoint.y) -> {
                    game.selectedBiome = environments[currentIndex]
                    game.goToMainMenu()
                    true
                }
                else -> false
            }
        }

        if (isTouched && !suppressDragThisTouch) {
            val dxScreen = Gdx.input.deltaX.toFloat()
            val worldPerPixel = WORLD_W / Gdx.graphics.width.toFloat()
            scrollX -= dxScreen * worldPerPixel
            dragging = true
        }

        if (!isTouched && wasTouchedLastFrame && dragging) {
            // Touch released — snap to nearest card
            val targetIndex = round(scrollX / CARD_SPACING).toInt().coerceIn(0, environments.size - 1)
            currentIndex = targetIndex
            dragging = false
        }

        if (!dragging) {
            val target = currentIndex * CARD_SPACING
            scrollX += (target - scrollX) * 0.25f
        }

        wasTouchedLastFrame = isTouched
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
