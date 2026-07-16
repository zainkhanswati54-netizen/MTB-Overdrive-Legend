package com.example.mbx.physics

import com.badlogic.gdx.physics.box2d.*

/**
 * Infinite procedural terrain: rolling hills built from a sum of sine waves
 * (deterministic per seed, so the same seed always produces the same ride).
 * Ground is generated in chunks ahead of the camera as static Box2D chain
 * bodies, and chunks that fall far enough behind the camera are destroyed
 * to keep the simulation cheap.
 */
class TerrainGenerator(
    private val world: World,
    private val seed: Float = 12.34f,
    private val baseY: Float = 0f,
    private val amplitude: Float = 1.5f
) {
    private val chunkWidth = 20f
    private val pointSpacing = 0.5f
    private val keepBehindMargin = 25f

    private data class Chunk(val startX: Float, val endX: Float, val body: Body)
    private val chunks = mutableListOf<Chunk>()
    private var generatedUpToX = 0f

    /** Ground height at a given world x — smooth, rideable rolling hills. */
    fun heightAt(x: Float): Float {
        val n1 = kotlin.math.sin((x * 0.045f + seed).toDouble()).toFloat() * amplitude
        val n2 = kotlin.math.sin((x * 0.11f + seed * 1.7f).toDouble()).toFloat() * amplitude * 0.35f
        val n3 = kotlin.math.sin((x * 0.27f + seed * 0.6f).toDouble()).toFloat() * amplitude * 0.12f
        return baseY + n1 + n2 + n3
    }

    /** Call every frame with the furthest x the camera/bike can currently see. */
    fun ensureGeneratedUpTo(x: Float) {
        while (generatedUpToX < x + chunkWidth) {
            generateChunk(generatedUpToX, generatedUpToX + chunkWidth)
            generatedUpToX += chunkWidth
        }
    }

    /** Call every frame with the current camera/bike x to free old chunks. */
    fun removeChunksBehind(x: Float) {
        val toRemove = chunks.filter { it.endX < x - keepBehindMargin }
        for (c in toRemove) {
            world.destroyBody(c.body)
        }
        chunks.removeAll(toRemove)
    }

    private fun generateChunk(startX: Float, endX: Float) {
        val pointCount = ((endX - startX) / pointSpacing).toInt() + 1
        val vertices = FloatArray(pointCount * 2)
        var x = startX
        var i = 0
        while (i < pointCount) {
            vertices[i * 2] = x
            vertices[i * 2 + 1] = heightAt(x)
            x += pointSpacing
            i++
        }

        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.StaticBody
            position.set(0f, 0f)
        }
        val body = world.createBody(bodyDef)

        val chainShape = ChainShape()
        chainShape.createChain(vertices)

        val fixtureDef = FixtureDef().apply {
            shape = chainShape
            friction = 0.9f
            density = 0f
        }
        body.createFixture(fixtureDef)
        chainShape.dispose()

        chunks.add(Chunk(startX, endX, body))
    }
}
