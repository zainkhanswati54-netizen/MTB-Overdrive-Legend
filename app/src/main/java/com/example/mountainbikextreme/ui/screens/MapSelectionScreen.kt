package com.example.mountainbikextreme.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mountainbikextreme.ui.components.Biome
import com.example.mountainbikextreme.ui.components.EnvironmentBackground
import com.example.mountainbikextreme.ui.theme.AccentWhite
import com.example.mountainbikextreme.ui.theme.ForestDeep
import com.example.mountainbikextreme.ui.theme.HillsDeep
import com.example.mountainbikextreme.ui.theme.SelectedGold
import kotlin.math.absoluteValue

private data class EnvironmentOption(val biome: Biome, val label: String)

private val environments = listOf(
    EnvironmentOption(Biome.MOUNTAINS, "Mountains"),
    EnvironmentOption(Biome.FOREST, "Forest"),
    EnvironmentOption(Biome.HILLS, "Hills"),
    EnvironmentOption(Biome.DESERT, "Desert")
)

@Composable
fun MapSelectionScreen(
    initiallySelected: Biome = Biome.FOREST,
    onBack: () -> Unit,
    onEnvironmentChosen: (Biome) -> Unit
) {
    val startIndex = environments.indexOfFirst { it.biome == initiallySelected }.coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = startIndex) { environments.size }
    var selectedIndex by remember { mutableStateOf(startIndex) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(HillsDeep, ForestDeep)))
    ) {
        // Back arrow
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
                .clickable { onBack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = AccentWhite.copy(alpha = 0.9f),
                modifier = Modifier.size(26.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Environment",
                color = AccentWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 60.dp),
                pageSpacing = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) { page ->
                val option = environments[page]
                val isSelected = page == pagerState.currentPage

                EnvironmentCard(
                    option = option,
                    isSelected = isSelected,
                    pagerState = pagerState,
                    page = page
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = environments[pagerState.currentPage].label,
                color = AccentWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Confirm selection button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(SelectedGold.copy(alpha = 0.9f))
                    .clickable {
                        selectedIndex = pagerState.currentPage
                        onEnvironmentChosen(environments[selectedIndex].biome)
                    }
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = ForestDeep,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Select",
                    color = ForestDeep,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun EnvironmentCard(
    option: EnvironmentOption,
    isSelected: Boolean,
    pagerState: PagerState,
    page: Int
) {
    // Scale down non-centered pages slightly, like the reference peek carousel
    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
    val scale = 1f - (pageOffset * 0.15f).coerceIn(0f, 0.15f)

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .graphicsLayerScale(scale)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isSelected) 3.dp else 2.dp,
                color = if (isSelected) SelectedGold else AccentWhite.copy(alpha = 0.7f),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        EnvironmentBackground(biome = option.biome, modifier = Modifier.clip(RoundedCornerShape(8.dp)))
    }
}

// Small helper so we don't need extra imports scattered around
private fun Modifier.graphicsLayerScale(scale: Float): Modifier = this.then(
    androidx.compose.ui.draw.scale(scale)
)
