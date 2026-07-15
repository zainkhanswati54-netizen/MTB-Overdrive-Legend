package com.mtbgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.mtbgame.MtbGame
import com.mtbgame.entities.Bike
import com.mtbgame.world.Terrain

/**
 * Gameplay screen: physics world update + terrain scroll + bike control + HUD.
 * Abhi ke liye rendering ShapeRenderer se ho rahi hai (placeholder graphics) —
 * baad mein hum isko textures/parallax art se replace karenge.
 */
class GameScreen(private val game: MtbGame) : Screen {

    private val world = World(com.badlogic.gdx.math.Vector2(0f, -20f), true)
    private val terrain = Terrain(world)
    private val bike = Bike(world, 0f, 3f)

    private val camera = OrthographicCamera()
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()

    private val VIEW_WIDTH = 18f
    private val VIEW_HEIGHT = 10f

    private var startX = bike.position().x
    private var bestDistance = 0f
    private var crashed = false
    private var elapsedTime = 0f

    private val debugRenderer = Box2DDebugRenderer()
    private var useDebugRender = false // true karke Box2D collision shapes dekh sakte ho testing ke liye

    init {
        camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT)
        bestDistance = game.prefs.getFloat("best_distance", 0f)
    }

    override fun render(delta: Float) {
        if (!crashed) {
            handleInput()
            world.step(1 / 60f, 6, 2)
            elapsedTime += delta

            // Naya terrain generate karo jab bike terrain ke end ke kareeb pahunche
            if (bike.position().x > terrain.furthestX() - 30f) {
                terrain.generateChunk()
            }

            if (bike.isCrashed()) {
                onCrash()
            }
        }

        // Camera bike ko follow kare (thoda aage dekhte hue, taaki upcoming terrain nazar aaye)
        camera.position.set(bike.position().x + 3f, VIEW_HEIGHT / 2f + 1f, 0f)
        camera.update()

        Gdx.gl.glClearColor(0.53f, 0.86f, 0.92f, 1f) // halka sky-blue background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        drawTerrain()
        drawBike()
        drawHud()

        if (useDebugRender) {
            debugRenderer.render(world, camera.combined)
        }
    }

    private fun handleInput() {
        val throttle = when {
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> 1f
            Gdx.input.isKeyPressed(Input.Keys.LEFT) -> -1f
            else -> 0f
        }
        bike.setThrottle(throttle)

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) bike.applyLean(1f)
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) bike.applyLean(-1f)

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) useDebugRender = !useDebugRender
    }

    private fun onCrash() {
        crashed = true
        val distance = bike.position().x - startX
        if (distance > bestDistance) {
            bestDistance = distance
            game.prefs.putFloat("best_distance", bestDistance)
            game.prefs.flush()
        }
        // 1.5 second baad Menu par wapas
        Gdx.app.postRunnable {
            Gdx.input.inputProcessor = null
        }
    }

    private fun drawTerrain() {
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.10f, 0.35f, 0.28f, 1f) // dark forest-green ground fill
        val points = terrain.groundPoints
        for (i in 0 until points.size - 1) {
            val (x1, y1) = points[i]
            val (x2, y2) = points[i + 1]
            // Simple filled quad neeche tak (ground ko solid dikhane ke liye)
            shapeRenderer.triangle(x1, y1, x2, y2, x2, -20f)
            shapeRenderer.triangle(x1, y1, x2, -20f, x1, -20f)
        }
        shapeRenderer.end()
    }

    private fun drawBike() {
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Wheels
        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.circle(bike.rearWheel.position.x, bike.rearWheel.position.y, Bike.WHEEL_RADIUS, 20)
        shapeRenderer.circle(bike.frontWheel.position.x, bike.frontWheel.position.y, Bike.WHEEL_RADIUS, 20)

        // Chassis (rough silhouette line jod ke)
        shapeRenderer.color = Color.BLACK
        val chassisPos = bike.chassis.position
        val angle = bike.chassis.angle
        val cos = Math.cos(angle.toDouble()).toFloat()
        val sin = Math.sin(angle.toDouble()).toFloat()
        // Simple frame line rear-wheel se front-wheel tak (visual approx)
        shapeRenderer.rectLine(
            bike.rearWheel.position.x, bike.rearWheel.position.y,
            bike.frontWheel.position.x, bike.frontWheel.position.y,
            0.08f
        )
        // Rider ka approx torso, chassis angle ke rotate hote hue
        val torsoX = chassisPos.x - sin * 0.3f
        val torsoY = chassisPos.y + cos * 0.3f
        shapeRenderer.rectLine(chassisPos.x, chassisPos.y, torsoX, torsoY, 0.15f)

        shapeRenderer.end()
    }

    private fun drawHud() {
        batch.projectionMatrix = batch.projectionMatrix.setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.begin()
        val distance = (bike.position().x - startX).coerceAtLeast(0f)
        font.draw(batch, "Distance: ${distance.toInt()}m", 20f, Gdx.graphics.height - 20f)
        font.draw(batch, "Best: ${bestDistance.toInt()}m", 20f, Gdx.graphics.height - 45f)
        if (crashed) {
            font.draw(batch, "CRASHED! Press R to restart", 20f, Gdx.graphics.height - 75f)
        } else {
            font.draw(batch, "Arrows: throttle/brake + lean | F1: debug view", 20f, 20f)
        }
        batch.end()

        if (crashed && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.setScreen(GameScreen(game))
            dispose()
        }
    }

    override fun resize(width: Int, height: Int) {}
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    override fun dispose() {
        world.dispose()
        shapeRenderer.dispose()
        batch.dispose()
        font.dispose()
        debugRenderer.dispose()
    }

    override fun show() {}
}
