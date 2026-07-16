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
import com.example.mbx.physics.BikePhysicsWorld
import com.example.mbx.physics.TerrainGenerator
import com.example.mbx.render.Biome
import com.example.mbx.render.IconRenderer
import com.example.mbx.render.Palettes
import kotlin.random.Random

private const val WORLD_W = 16f
private const val WORLD_H = 9f
private const val FIXED_STEP = 1f / 60f

class GameplayScreen(private val game: MountainBikeGame, private val biome: Biome) : Screen {

    // World camera — follows the bike, used for terrain/bike rendering.
    private val worldCamera = OrthographicCamera()
    private val worldViewport: Viewport = FitViewport(WORLD_W, WORLD_H, worldCamera)

    // HUD camera — fixed, used for buttons/text so they stay put on screen.
    private val hudCamera = OrthographicCamera()
    private val hudViewport: Viewport = FitViewport(WORLD_W, WORLD_H, hudCamera)

    private val physics = BikePhysicsWorld()
    private val terrain = TerrainGenerator(physics.world, seed = biome.ordinal * 37.7f + 4.2f)
    private val palette = Palettes.forBiome(biome)

    private var accumulator = 0f
    private var elapsed = 0f
    private var paused = false
    private var runCommitted = false

    private data class Coin(val x: Float, val y: Float, var collected: Boolean = false)
    private val coins = mutableListOf<Coin>()
    private var coinsCollected = 0
    private var nextCoinChunkX = 0f
    private val coinRng = Random((biome.ordinal * 991 + 17))

    private val touchPoint = Vector3()
    private val wasTouched = BooleanArray(4)

    // HUD-space (fixed 0..16, 0..9) button regions
    private val pauseRegion = RectRegion(0.3f, WORLD_H - 1.1f, 0.9f, 0.9f)
    private val brakeRegion = CircleRegion(WORLD_W - 1.6f, 1.5f, 0.75f)
    private val pedalRegion = CircleRegion(WORLD_W - 0.6f, 1.9f, 1.0f)
    private val leanBackRegion = CircleRegion(1.1f, 1.5f, 0.7f)
    private val leanForwardRegion = CircleRegion(2.6f, 1.5f, 0.7f)

    private val white = Color(0.949f, 0.969f, 0.965f, 1f)
    private val gold = Color(0.910f, 0.769f, 0.408f, 1f)
    private val overlayDark = Color(0.043f, 0.180f, 0.200f, 0.78f)

    private var spawnX = 2f

    override fun show() {
        val spawnY = terrain.heightAt(spawnX) + 1.0f
        physics.spawnBike(spawnX, spawnY)
        worldCamera.position.set(spawnX, spawnY, 0f)
        terrain.ensureGeneratedUpTo(spawnX + WORLD_W)
        hudCamera.position.set(WORLD_W / 2f, WORLD_H / 2f, 0f)
    }

    override fun render(delta: Float) {
        if (!paused && !physics.crashed) {
            handleGameplayInput()
            accumulator += delta
            while (accumulator >= FIXED_STEP) {
                physics.step(FIXED_STEP)
                accumulator -= FIXED_STEP
            }
            elapsed += delta
            updateWorldStreaming()
            updateCoins()
        }

        if (physics.crashed && !runCommitted) {
            commitRunStats()
        }

        handleMetaInput()
        updateCameraFollow()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        drawWorld()
        drawHud()
    }

    // ---------------------------------------------------------------- input

    private fun handleGameplayInput() {
        var pedal = false
        var brake = false
        var leanFwd = false
        var leanBack = false

        for (p in 0 until 4) {
            if (!Gdx.input.isTouched(p)) continue
            touchPoint.set(Gdx.input.getX(p).toFloat(), Gdx.input.getY(p).toFloat(), 0f)
            hudViewport.unproject(touchPoint)
            when {
                pedalRegion.contains(touchPoint.x, touchPoint.y) -> pedal = true
                brakeRegion.contains(touchPoint.x, touchPoint.y) -> brake = true
                leanForwardRegion.contains(touchPoint.x, touchPoint.y) -> leanFwd = true
                leanBackRegion.contains(touchPoint.x, touchPoint.y) -> leanBack = true
            }
        }

        when {
            brake -> physics.applyBrake()
            pedal -> physics.setPedalInput(1f)
            else -> physics.setPedalInput(0f)
        }
        if (leanFwd) physics.lean(-1f)
        if (leanBack) physics.lean(1f)
    }

    /** Pause button, resume/restart taps, menu taps — checked every frame regardless of pause state. */
    private fun handleMetaInput() {
        for (p in 0 until 4) {
            val isDown = Gdx.input.isTouched(p)
            val justDown = isDown && !wasTouched[p]
            wasTouched[p] = isDown
            if (!justDown) continue

            touchPoint.set(Gdx.input.getX(p).toFloat(), Gdx.input.getY(p).toFloat(), 0f)
            hudViewport.unproject(touchPoint)

            if (physics.crashed) {
                // Tap anywhere to restart after a crash
                restartRun()
                continue
            }

            if (pauseRegion.contains(touchPoint.x, touchPoint.y)) {
                paused = !paused
                continue
            }

            if (paused) {
                // "Menu" tap zone — center of the pause overlay
                val menuRegion = RectRegion(WORLD_W / 2f - 1.5f, WORLD_H / 2f - 1.6f, 3f, 0.8f)
                if (menuRegion.contains(touchPoint.x, touchPoint.y)) {
                    if (!runCommitted) commitRunStats()
                    game.goToMainMenu()
                }
            }
        }
    }

    private fun restartRun() {
        physics.reset()
        elapsed = 0f
        coinsCollected = 0
        coins.clear()
        nextCoinChunkX = 0f
        runCommitted = false
        worldCamera.position.set(spawnX, terrain.heightAt(spawnX) + 1.0f, 0f)
    }

    private fun commitRunStats() {
        runCommitted = true
        val distance = currentDistanceMeters()
        val prefs = Gdx.app.getPreferences("mbx_prefs")
        val best = prefs.getFloat("best_distance_m", 0f)
        if (distance > best) prefs.putFloat("best_distance_m", distance)
        prefs.putFloat("total_distance_m", prefs.getFloat("total_distance_m", 0f) + distance)
        prefs.flush()
    }

    private fun currentDistanceMeters(): Float = (physics.chassis.position.x - spawnX).coerceAtLeast(0f)

    // ------------------------------------------------------------- updates

    private fun updateWorldStreaming() {
        val aheadX = physics.chassis.position.x + WORLD_W
        terrain.ensureGeneratedUpTo(aheadX)
        terrain.removeChunksBehind(physics.chassis.position.x)

        while (nextCoinChunkX < aheadX) {
            val cx = nextCoinChunkX + 4f + coinRng.nextFloat() * 4f
            val cy = terrain.heightAt(cx) + 1.1f
            coins.add(Coin(cx, cy))
            nextCoinChunkX += 8f
        }
        coins.removeAll { it.collected && it.x < physics.chassis.position.x - 10f }
    }

    private fun updateCoins() {
        val bx = physics.chassis.position.x
        val by = physics.chassis.position.y
        for (c in coins) {
            if (c.collected) continue
            val dx = c.x - bx
            val dy = c.y - by
            if (dx * dx + dy * dy < 0.45f * 0.45f) {
                c.collected = true
                coinsCollected++
            }
        }
    }

    private fun updateCameraFollow() {
        val targetX = physics.chassis.position.x + 3f
        val targetY = physics.chassis.position.y + 1.2f
        worldCamera.position.x += (targetX - worldCamera.position.x) * 0.08f
        worldCamera.position.y += (targetY - worldCamera.position.y) * 0.05f
        worldCamera.update()
    }

    // ------------------------------------------------------------- drawing

    private fun drawWorld() {
        val sr = game.assets.shapeRenderer
        sr.projectionMatrix = worldCamera.combined

        val halfW = WORLD_W / 2f + 2f
        val left = worldCamera.position.x - halfW
        val right = worldCamera.position.x + halfW

        sr.begin(ShapeRenderer.ShapeType.Filled)
        // Sky
        sr.rect(
            left, worldCamera.position.y - WORLD_H, right - left, WORLD_H * 3f,
            palette.skyBottom, palette.skyBottom, palette.skyTop, palette.skyTop
        )

        // Ground fill, sampled at 0.5m steps
        val bottom = worldCamera.position.y - WORLD_H
        sr.setColor(palette.ground)
        var x = left
        val step = 0.5f
        while (x < right) {
            val nextX = (x + step).coerceAtMost(right)
            val y1 = terrain.heightAt(x)
            val y2 = terrain.heightAt(nextX)
            sr.triangle(x, bottom, nextX, bottom, nextX, y2)
            sr.triangle(x, bottom, nextX, y2, x, y1)
            x = nextX
        }
        // Grass strip along the top of the terrain
        sr.setColor(palette.layers.first())
        x = left
        while (x < right) {
            val nextX = (x + step).coerceAtMost(right)
            val y1 = terrain.heightAt(x)
            val y2 = terrain.heightAt(nextX)
            sr.triangle(x, y1 - 0.12f, nextX, y2 - 0.12f, nextX, y2 + 0.12f)
            sr.triangle(x, y1 - 0.12f, nextX, y2 + 0.12f, x, y1 + 0.12f)
            x = nextX
        }

        // Coins
        for (c in coins) {
            if (c.collected) continue
            if (c.x < left || c.x > right) continue
            sr.setColor(gold)
            sr.circle(c.x, c.y, 0.16f, 16)
        }
        sr.end()

        drawBike(sr)
    }

    private fun drawBike(sr: ShapeRenderer) {
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.setColor(white)
        drawWheel(sr, physics.rearWheel)
        drawWheel(sr, physics.frontWheel)
        drawRotatedBox(sr, physics.chassis, 0.62f, 0.16f)
        drawRotatedBox(sr, physics.torso, 0.10f, 0.30f)
        sr.circle(physics.head.position.x, physics.head.position.y, 0.16f, 16)
        sr.end()
    }

    private fun drawWheel(sr: ShapeRenderer, wheel: com.badlogic.gdx.physics.box2d.Body) {
        sr.circle(wheel.position.x, wheel.position.y, BikePhysicsWorld.WHEEL_RADIUS, 20)
    }

    private fun drawRotatedBox(sr: ShapeRenderer, body: com.badlogic.gdx.physics.box2d.Body, hw: Float, hh: Float) {
        val cos = MathUtils.cos(body.angle)
        val sin = MathUtils.sin(body.angle)
        val cx = body.position.x
        val cy = body.position.y

        fun rot(px: Float, py: Float): Pair<Float, Float> =
            Pair(cx + px * cos - py * sin, cy + px * sin + py * cos)

        val (x1, y1) = rot(-hw, -hh)
        val (x2, y2) = rot(hw, -hh)
        val (x3, y3) = rot(hw, hh)
        val (x4, y4) = rot(-hw, hh)

        sr.triangle(x1, y1, x2, y2, x3, y3)
        sr.triangle(x1, y1, x3, y3, x4, y4)
    }

    private fun drawHud() {
        val sr = game.assets.shapeRenderer
        sr.projectionMatrix = hudCamera.combined

        IconRenderer.drawSettingsGear(sr, pauseRegion.x + pauseRegion.w / 2f, pauseRegion.y + pauseRegion.h / 2f, 0.7f, white)

        sr.begin(ShapeRenderer.ShapeType.Line)
        sr.setColor(Color(white).apply { a = 0.85f })
        sr.circle(brakeRegion.cx, brakeRegion.cy, brakeRegion.r)
        sr.line(brakeRegion.cx - brakeRegion.r * 0.7f, brakeRegion.cy - brakeRegion.r * 0.7f, brakeRegion.cx + brakeRegion.r * 0.7f, brakeRegion.cy + brakeRegion.r * 0.7f)
        sr.circle(leanBackRegion.cx, leanBackRegion.cy, leanBackRegion.r)
        sr.circle(leanForwardRegion.cx, leanForwardRegion.cy, leanForwardRegion.r)
        sr.end()

        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.setColor(Color(white).apply { a = 0.18f })
        sr.circle(pedalRegion.cx, pedalRegion.cy, pedalRegion.r)
        sr.end()

        val batch = game.assets.batch
        val layout = game.assets.layout
        batch.projectionMatrix = hudCamera.combined
        batch.begin()

        game.assets.bodyFont.color = white
        val mm = (elapsed / 60).toInt()
        val ss = (elapsed % 60).toInt()
        layout.setText(game.assets.bodyFont, String.format("%02d:%02d", mm, ss))
        game.assets.bodyFont.draw(batch, layout, WORLD_W - layout.width - 0.3f, WORLD_H - 0.3f)

        layout.setText(game.assets.bodyFont, "${currentDistanceMeters().toInt()}m")
        game.assets.bodyFont.draw(batch, layout, WORLD_W - layout.width - 0.3f, WORLD_H - 0.7f)

        layout.setText(game.assets.bodyFont, "Coins: $coinsCollected")
        game.assets.bodyFont.draw(batch, layout, WORLD_W - layout.width - 0.3f, WORLD_H - 1.1f)

        if (paused) {
            batch.end()
            drawOverlay("Paused", "Tap gear to resume  ·  tap below for menu", true)
            return
        }

        if (physics.crashed) {
            batch.end()
            drawOverlay("Crashed!", "Distance: ${currentDistanceMeters().toInt()}m  ·  Tap to restart", false)
            return
        }

        batch.end()
    }

    private fun drawOverlay(title: String, subtitle: String, showMenuButton: Boolean) {
        val sr = game.assets.shapeRenderer
        sr.projectionMatrix = hudCamera.combined
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.setColor(overlayDark)
        sr.rect(0f, 0f, WORLD_W, WORLD_H)
        if (showMenuButton) {
            sr.setColor(gold.apply { a = 0.9f })
            sr.rect(WORLD_W / 2f - 1.5f, WORLD_H / 2f - 1.6f, 3f, 0.8f)
        }
        sr.end()

        val batch = game.assets.batch
        val layout = game.assets.layout
        batch.projectionMatrix = hudCamera.combined
        batch.begin()
        game.assets.titleFont.color = white
        layout.setText(game.assets.titleFont, title)
        game.assets.titleFont.draw(batch, layout, WORLD_W / 2f - layout.width / 2f, WORLD_H / 2f + 0.6f)

        game.assets.bodyFont.color = white
        layout.setText(game.assets.bodyFont, subtitle)
        game.assets.bodyFont.draw(batch, layout, WORLD_W / 2f - layout.width / 2f, WORLD_H / 2f)

        if (showMenuButton) {
            game.assets.bodyFont.color = Color(0.055f, 0.231f, 0.212f, 1f)
            layout.setText(game.assets.bodyFont, "Menu")
            game.assets.bodyFont.draw(batch, layout, WORLD_W / 2f - layout.width / 2f, WORLD_H / 2f - 1.2f)
        }
        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        worldViewport.update(width, height, false)
        hudViewport.update(width, height, true)
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    override fun dispose() {
        physics.dispose()
    }

    private data class RectRegion(val x: Float, val y: Float, val w: Float, val h: Float) {
        fun contains(px: Float, py: Float) = px in x..(x + w) && py in y..(y + h)
    }

    private data class CircleRegion(val cx: Float, val cy: Float, val r: Float) {
        fun contains(px: Float, py: Float): Boolean {
            val dx = px - cx
            val dy = py - cy
            return dx * dx + dy * dy <= r * r
        }
    }
}
