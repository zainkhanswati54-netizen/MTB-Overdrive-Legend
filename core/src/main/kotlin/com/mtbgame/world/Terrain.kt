package com.mtbgame.world

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import kotlin.random.Random

/**
 * Procedural downhill terrain.
 * Random-walk based height generation (jaise real endless-runner bike games karte hain):
 * har agle point ki height thodi upar/neeche shift hoti hai previous se, taaki
 * smooth rolling hills bane, na ki achanak spikes.
 *
 * Terrain "chunks" mein generate hota hai — jaise bike aage badhti hai,
 * naye chunks add hote hain aur bahut peeche wale chunks memory se hata diye jaate hain.
 */
class Terrain(private val world: World, private val seed: Long = System.currentTimeMillis()) {

    companion object {
        const val POINT_SPACING = 1.2f      // do consecutive ground points ke beech horizontal distance (meters)
        const val CHUNK_POINTS = 40          // ek chunk mein kitne points
    }

    private val random = Random(seed)

    // Har point ki (x, y) world coordinate meters mein
    val groundPoints = mutableListOf<Pair<Float, Float>>()

    private var lastHeight = 0f
    private var lastX = 0f
    private var groundBody: Body

    // Downhill slope trend + random bumps ke liye state
    private var trendSlope = -0.15f   // overall downhill tendency
    private var bumpTimer = 0

    init {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        groundBody = world.createBody(bodyDef)

        // Start ke pehle kuch points flat rakhte hain taaki bike sahi se spawn ho
        groundPoints.add(Pair(-10f, 0f))
        groundPoints.add(Pair(0f, 0f))
        lastX = 0f
        lastHeight = 0f

        generateChunk()
        generateChunk()
    }

    /** Ek naya chunk of terrain points generate karke Box2D edge chain banata hai */
    fun generateChunk() {
        val newPoints = mutableListOf<Pair<Float, Float>>()

        repeat(CHUNK_POINTS) {
            lastX += POINT_SPACING

            // Random bump height change, downhill trend ke saath
            bumpTimer++
            val bumpiness = MathUtils.random(-0.6f, 0.6f)
            var deltaY = trendSlope * POINT_SPACING + bumpiness

            // Har ~15 points ke baad thoda bada jump/dip daalte hain, variety ke liye
            if (bumpTimer % 15 == 0) {
                deltaY += MathUtils.random(-2.5f, 1.5f)
            }

            lastHeight += deltaY
            // Height ko bahut zyada steep hone se roko
            lastHeight = MathUtils.clamp(lastHeight, lastX * -0.6f, lastX * -0.05f)

            newPoints.add(Pair(lastX, lastHeight))
        }

        groundPoints.addAll(newPoints)
        buildCollisionChain(newPoints)
    }

    /** Diye gaye points ke liye ek Box2D chain shape (ground collider) banata hai */
    private fun buildCollisionChain(points: List<Pair<Float, Float>>) {
        if (points.size < 2) return

        val vertices = Array(points.size) { i ->
            com.badlogic.gdx.math.Vector2(points[i].first, points[i].second)
        }

        val chainShape = ChainShape()
        chainShape.createChain(vertices)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = chainShape
        fixtureDef.friction = 0.9f
        fixtureDef.restitution = 0f
        fixtureDef.filter.categoryBits = CATEGORY_GROUND

        val fixture = groundBody.createFixture(fixtureDef)
        fixture.userData = "ground"

        chainShape.dispose()
    }

    /** Height at a given x (interpolated) — camera/spawn logic ke kaam aata hai */
    fun heightAt(x: Float): Float {
        for (i in 0 until groundPoints.size - 1) {
            val (x1, y1) = groundPoints[i]
            val (x2, y2) = groundPoints[i + 1]
            if (x in x1..x2) {
                val t = (x - x1) / (x2 - x1)
                return y1 + (y2 - y1) * t
            }
        }
        return groundPoints.lastOrNull()?.second ?: 0f
    }

    fun furthestX(): Float = groundPoints.lastOrNull()?.first ?: 0f
}

const val CATEGORY_GROUND: Short = 0x0001
const val CATEGORY_BIKE: Short = 0x0002
