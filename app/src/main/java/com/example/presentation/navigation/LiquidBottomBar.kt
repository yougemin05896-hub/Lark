package com.example.presentation.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.skydoves.cloudy.cloudy
import com.example.ui.components.LiquidGlassBox
import com.example.ui.theme.GlassIcons
import kotlin.math.roundToInt

data class NavItem(val title: String, val icon: ImageVector, val route: String)

val navItems = listOf(
    NavItem("Home", GlassIcons.Play, "home"),
    NavItem("Playlists", GlassIcons.Subtitles, "playlists"),
    NavItem("Settings", GlassIcons.Settings, "settings")
)

@Composable
fun LiquidBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Keep track of the X coordinate of the center of each nav item.
    var itemPositions by remember { mutableStateOf(List(navItems.size) { 0f }) }
    
    val selectedIndex = navItems.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    var previousIndex by remember { mutableStateOf(selectedIndex) }

    // When selection changes, we remember where we came from to compute direction.
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != previousIndex) {
            previousIndex = selectedIndex
        }
    }

    val targetX = itemPositions.getOrElse(selectedIndex) { 0f }
    val previousTargetX = itemPositions.getOrElse(previousIndex) { 0f }
    val direction = targetX - previousTargetX

    // Physics Engine: Spring with DampingRatioMediumBouncy.
    // To warp the shape like liquid, the leading edge travels faster than the trailing edge!

    val leadingEdge by animateFloatAsState(
        targetValue = targetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = if (direction >= 0) Spring.StiffnessMedium else Spring.StiffnessLow
        ),
        label = "leadingEdgeX"
    )

    val trailingEdge by animateFloatAsState(
        targetValue = targetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = if (direction >= 0) Spring.StiffnessLow else Spring.StiffnessMedium
        ),
        label = "trailingEdgeX"
    )

    // Calculate dynamic physical width for stretch effect
    val baseWidth = with(androidx.compose.ui.platform.LocalDensity.current) { 64.dp.toPx() }
    val halfWidth = baseWidth / 2f
    
    val currentLeft = minOf(leadingEdge, trailingEdge) - halfWidth
    val currentRight = maxOf(leadingEdge, trailingEdge) + halfWidth
    val currentWidth = maxOf(baseWidth, currentRight - currentLeft)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // The Sleek Glass Container Base
        Box(
            modifier = Modifier.fillMaxSize().cloudy(radius = 25).background(Color.Black.copy(alpha = 0.5f), CircleShape)
        )
        
        // The Float/Snap Liquid Capsule Indicator over the Glass
        if (itemPositions.isNotEmpty() && itemPositions[selectedIndex] > 0f) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(currentLeft.roundToInt(), 0) }
                    .width(with(androidx.compose.ui.platform.LocalDensity.current) { currentWidth.toDp() })
                    .height(48.dp)
                    // Brilliant 100% white overlay, fully sharp
                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
            )
        }

        // Navigation Elements (Sharp Icons on top of glass)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = currentRoute == item.route
                Column(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            val newPositions = itemPositions.toMutableList()
                            // X position relative to the row parent center
                            newPositions[index] = coordinates.positionInParent().x + (coordinates.size.width / 2f)
                            itemPositions = newPositions
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNavigate(item.route)
                        }
                        .width(64.dp)
                        .height(56.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.7f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
