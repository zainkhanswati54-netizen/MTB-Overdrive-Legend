package com.example.mountainbikextreme.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.example.mountainbikextreme.ui.theme.*
import kotlin.random.Random

enum class Biome { FOREST, HILLS, DESERT, MOUNTAINS }

private data class BiomePalette(
    val skyTop: Color,
    val skyBottom: Color,
    val layers: List<Color>, // back to front
    val ground: Color
)

private fun paletteFor(biome: Biome): BiomePalette = when (biome) {
    Biome.FOREST -> BiomePalette(
        skyTop = SkyLight, skyBottom = SkyMid,
        layers = listOf(ForestPale, ForestLight, ForestMid),
        ground = ForestDeep
    )
    Biome.HILLS -> BiomePalette(
        skyTop = HillsSky, skyBottom = SkyMid,
        layers = listOf(ForestLight, HillsMid, ForestMid),
        ground = HillsDeep
    )
    Biome.DESERT -> BiomePalette(
        skyTop = DesertSky, skyBottom = Color(0xFFE8B27A),
        layers = listOf(Color(0xFFD9A671), DesertMid, Color(0xFF6B4530)),
        ground = DesertGround
    )
    Biome.MOUNTAINS -> BiomePalette(
        skyTop = MountainsSky, skyBottom = SkyMid,
        layers = listOf(Color(0xFFAFCBD4), MountainsMid, Color(0xFF4C7885)),
        ground = MountainsGround
    )
}

/**
 * Draws a procedural parallax silhouette scene for the given biome.
 * Pure vector/Canvas — no bitmap assets needed.
 */
@Composable
fun EnvironmentBackground(biome: Biome, modifier: Modifier = Modifier, seed: Int = biome.ordinal * 97) {
    val palette = paletteFor(biome)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(palette.skyTop, palette.skyBottom)))
    ) {
        val w = size.width
        val h = size.height
        val rng = Random(seed)

        // Rolling ground silhouette layers (back to front), each with its own hill curve
        val layerCount = palette.layers.size
        for (i in 0 until layerCount) {
            val baseline = h * (0.60f + i * 0.10f)
            val amplitude = h * (0.05f + i * 0.02f)
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, baseline)
                var x = 0f
                val step = w / 6f
                var prevY = baseline
                while (x <= w) {
                    val nextX = (x + step).coerceAtMost(w)
                    val nextY = baseline + (rng.nextFloat() - 0.5f) * amplitude * 2f
                    quadraticBezierTo(
                        (x + nextX) / 2f, prevY,
                        nextX, nextY
                    )
                    prevY = nextY
                    x += step
                }
                lineTo(w, h)
                close()
            }
            drawPath(path, color = palette.layers[i])

            // Scatter simple tree / cactus silhouettes on this layer for forest/hills/mountains
            if (biome != Biome.DESERT) {
                val treeCount = 6 + i * 3
                repeat(treeCount) {
                    val tx = rng.nextFloat() * w
                    val ty = baseline - amplitude * 0.3f
                    val scale = 0.6f + i * 0.35f
                    drawPineTree(Offset(tx, ty), scale * (h * 0.09f), palette.layers[i])
                }
            } else if (i == layerCount - 1) {
                repeat(5) {
                    val cx = rng.nextFloat() * w
                    val cy = baseline - amplitude * 0.2f
                    drawCactus(Offset(cx, cy), h * 0.10f, palette.ground)
                }
            }
        }

        // Foreground ground
        drawRect(
            color = palette.ground,
            topLeft = Offset(0f, h * 0.88f),
            size = androidx.compose.ui.geometry.Size(w, h * 0.12f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPineTree(
    base: Offset,
    treeHeight: Float,
    color: Color
) {
    val trunkW = treeHeight * 0.06f
    val path = Path().apply {
        moveTo(base.x, base.y)
        lineTo(base.x - treeHeight * 0.35f, base.y - treeHeight * 0.55f)
        lineTo(base.x - treeHeight * 0.15f, base.y - treeHeight * 0.55f)
        lineTo(base.x - treeHeight * 0.45f, base.y - treeHeight * 0.9f)
        lineTo(base.x - treeHeight * 0.18f, base.y - treeHeight * 0.9f)
        lineTo(base.x, base.y - treeHeight * 1.35f)
        lineTo(base.x + treeHeight * 0.18f, base.y - treeHeight * 0.9f)
        lineTo(base.x + treeHeight * 0.45f, base.y - treeHeight * 0.9f)
        lineTo(base.x + treeHeight * 0.15f, base.y - treeHeight * 0.55f)
        lineTo(base.x + treeHeight * 0.35f, base.y - treeHeight * 0.55f)
        close()
    }
    drawPath(path, color = color)
    drawRect(
        color = color,
        topLeft = Offset(base.x - trunkW / 2f, base.y),
        size = androidx.compose.ui.geometry.Size(trunkW, treeHeight * 0.12f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCactus(
    base: Offset,
    cactusHeight: Float,
    color: Color
) {
    val w = cactusHeight * 0.18f
    drawRect(color, topLeft = Offset(base.x - w / 2f, base.y - cactusHeight), size = androidx.compose.ui.geometry.Size(w, cactusHeight))
    drawRect(color, topLeft = Offset(base.x - w * 2.2f, base.y - cactusHeight * 0.55f), size = androidx.compose.ui.geometry.Size(w, cactusHeight * 0.5f))
    drawRect(color, topLeft = Offset(base.x + w * 1.2f, base.y - cactusHeight * 0.7f), size = androidx.compose.ui.geometry.Size(w, cactusHeight * 0.5f))
}
