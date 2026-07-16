package com.example.mbx.render

import com.badlogic.gdx.graphics.Color

enum class Biome(val label: String) {
    MOUNTAINS("Mountains"),
    FOREST("Forest"),
    HILLS("Hills"),
    DESERT("Desert")
}

data class BiomePalette(
    val skyTop: Color,
    val skyBottom: Color,
    val layers: List<Color>, // back to front
    val ground: Color
)

object Palettes {
    private val forestPale = Color(0.435f, 0.733f, 0.663f, 1f)
    private val forestLight = Color(0.243f, 0.549f, 0.494f, 1f)
    private val forestMid = Color(0.118f, 0.365f, 0.325f, 1f)
    private val forestDeep = Color(0.055f, 0.231f, 0.212f, 1f)

    private val hillsMid = Color(0.078f, 0.322f, 0.302f, 1f)
    private val hillsDeep = Color(0.039f, 0.180f, 0.200f, 1f)
    private val hillsSky = Color(0.686f, 0.918f, 0.949f, 1f)

    private val skyLight = Color(0.725f, 0.941f, 0.918f, 1f)
    private val skyMid = Color(0.498f, 0.863f, 0.827f, 1f)

    private val desertSky = Color(0.957f, 0.851f, 0.627f, 1f)
    private val desertSky2 = Color(0.910f, 0.698f, 0.478f, 1f)
    private val desertTan1 = Color(0.851f, 0.651f, 0.443f, 1f)
    private val desertTan2 = Color(0.788f, 0.561f, 0.369f, 1f)
    private val desertBrown = Color(0.420f, 0.271f, 0.188f, 1f)
    private val desertGround = Color(0.227f, 0.125f, 0.075f, 1f)

    private val mountainsSky = Color(0.851f, 0.953f, 1f, 1f)
    private val mountainsGrey1 = Color(0.686f, 0.796f, 0.831f, 1f)
    private val mountainsGrey2 = Color(0.498f, 0.686f, 0.761f, 1f)
    private val mountainsGrey3 = Color(0.298f, 0.471f, 0.522f, 1f)
    private val mountainsGround = Color(0.200f, 0.357f, 0.400f, 1f)

    fun forestPalette() = BiomePalette(skyLight, skyMid, listOf(forestPale, forestLight, forestMid), forestDeep)
    fun hillsPalette() = BiomePalette(hillsSky, skyMid, listOf(forestLight, hillsMid, forestMid), hillsDeep)
    fun desertPalette() = BiomePalette(desertSky, desertSky2, listOf(desertTan1, desertTan2, desertBrown), desertGround)
    fun mountainsPalette() = BiomePalette(mountainsSky, skyMid, listOf(mountainsGrey1, mountainsGrey2, mountainsGrey3), mountainsGround)

    fun forBiome(biome: Biome): BiomePalette = when (biome) {
        Biome.FOREST -> forestPalette()
        Biome.HILLS -> hillsPalette()
        Biome.DESERT -> desertPalette()
        Biome.MOUNTAINS -> mountainsPalette()
    }
}
