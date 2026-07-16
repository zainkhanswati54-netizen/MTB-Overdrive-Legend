package com.example.mbx.render

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.random.Random

/**
 * Draws procedural, deterministic parallax scenes (sky gradient + layered hills
 * with pine trees / cacti) entirely with ShapeRenderer primitives.
 * No bitmap/image assets required — easy to swap for real art later.
 */
object ParallaxRenderer {

    fun render(
        sr: ShapeRenderer,
        biome: Biome,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        seed: Long = biome.ordinal.toLong() * 97L
    ) {
        val palette = Palettes.forBiome(biome)
        val rng = Random(seed)

        sr.begin(ShapeRenderer.ShapeType.Filled)

        // Sky gradient
        sr.rect(
            x, y, width, height,
            palette.skyBottom, palette.skyBottom, palette.skyTop, palette.skyTop
        )

        val layerCount = palette.layers.size
        for (i in 0 until layerCount) {
            val baselineFrac = 0.60f + i * 0.10f
            val amplitudeFrac = 0.05f + i * 0.02f
            val baselineY = y + height * baselineFrac
            val amplitude = height * amplitudeFrac

            drawHillLayer(sr, x, y, width, height, baselineY, amplitude, palette.layers[i], rng)

            if (biome != Biome.DESERT) {
                val treeCount = 5 + i * 3
                repeat(treeCount) {
                    val tx = x + rng.nextFloat() * width
                    val ty = baselineY - amplitude * 0.3f
                    val scale = (0.6f + i * 0.35f) * height * 0.11f
                    drawPineTree(sr, tx, ty, scale, palette.layers[i])
                }
            } else if (i == layerCount - 1) {
                repeat(4) {
                    val cx = x + rng.nextFloat() * width
                    val cy = baselineY - amplitude * 0.2f
                    drawCactus(sr, cx, cy, height * 0.11f, palette.ground)
                }
            }
        }

        // Foreground ground strip
        sr.setColor(palette.ground)
        sr.rect(x, y, width, height * 0.12f)

        sr.end()
    }

    private fun drawHillLayer(
        sr: ShapeRenderer,
        x: Float, y: Float, width: Float, height: Float,
        baselineY: Float, amplitude: Float, color: Color, rng: Random
    ) {
        val segments = 6
        val step = width / segments
        var prevX = x
        var prevY = baselineY + (rng.nextFloat() - 0.5f) * amplitude * 2f
        sr.setColor(color)
        for (i in 1..segments) {
            val curX = (x + i * step).coerceAtMost(x + width)
            val curY = baselineY + (rng.nextFloat() - 0.5f) * amplitude * 2f
            // Fill quad from bottom of card up to the jagged top edge
            sr.triangle(prevX, y, curX, y, curX, curY)
            sr.triangle(prevX, y, curX, curY, prevX, prevY)
            prevX = curX
            prevY = curY
        }
    }

    private fun drawPineTree(sr: ShapeRenderer, baseX: Float, baseY: Float, treeHeight: Float, color: Color) {
        sr.setColor(color)
        // Three stacked triangle tiers
        sr.triangle(baseX - treeHeight * 0.35f, baseY - treeHeight * 0.55f, baseX + treeHeight * 0.35f, baseY - treeHeight * 0.55f, baseX, baseY - treeHeight * 0.95f)
        sr.triangle(baseX - treeHeight * 0.30f, baseY - treeHeight * 0.85f, baseX + treeHeight * 0.30f, baseY - treeHeight * 0.85f, baseX, baseY - treeHeight * 1.15f)
        sr.triangle(baseX - treeHeight * 0.22f, baseY - treeHeight * 1.05f, baseX + treeHeight * 0.22f, baseY - treeHeight * 1.05f, baseX, baseY - treeHeight * 1.35f)
        // Trunk
        val trunkW = treeHeight * 0.06f
        sr.rect(baseX - trunkW / 2f, baseY - treeHeight * 0.12f, trunkW, treeHeight * 0.12f)
    }

    private fun drawCactus(sr: ShapeRenderer, baseX: Float, baseY: Float, cactusHeight: Float, color: Color) {
        sr.setColor(color)
        val w = cactusHeight * 0.18f
        sr.rect(baseX - w / 2f, baseY, w, cactusHeight)
        sr.rect(baseX - w * 2.2f, baseY + cactusHeight * 0.35f, w, cactusHeight * 0.5f)
        sr.rect(baseX + w * 1.2f, baseY + cactusHeight * 0.2f, w, cactusHeight * 0.5f)
    }
}
