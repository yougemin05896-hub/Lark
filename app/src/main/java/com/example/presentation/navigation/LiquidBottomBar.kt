package com.example.presentation.navigation

import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.skydoves.cloudy.cloudy
import com.skydoves.cloudy.liquidGlass
import kotlin.math.roundToInt

// =========================================================================================
// 1. DATA MODELS & CONFIGURATIONS
// =========================================================================================

/**
 * Enterprise level Navigation Item definition.
 * Supports localized titles, dynamic icons, and routing.
 */
data class NavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Mocking GlassIcons to prevent unresolved references.
 * In a real Enterprise app, these would come from a dedicated Design System module.
 */
object GlassIcons {
    val Play = Icons.Filled.PlayArrow
    val Subtitles = Icons.Filled.Home // Replaced with Home for generic structure
    val Settings = Icons.Filled.Settings
}

val navItems = listOf(
    NavItem("Home", GlassIcons.Play, "home"),
    NavItem("Playlists", GlassIcons.Subtitles, "playlists"),
    NavItem("Settings", GlassIcons.Settings, "settings")
)

// =========================================================================================
// 2. MAIN COMPOSABLE: ENTERPRISE LIQUID GLASS BOTTOM BAR
// =========================================================================================

/**
 * A highly advanced, physics-based liquid glass bottom navigation bar.
 * It features optical refraction, chromatic dispersion, and viscoelastic spring animations.
 * * @param currentRoute The currently active navigation route.
 * @param onNavigate Callback triggered when a new route is selected.
 * @param modifier Optional modifier for the container.
 */
@Composable
fun LiquidBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // ---- STATE MANAGEMENT ----
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    
    // Store the exact X coordinates of the center of each icon dynamically
    var itemPositions by remember { mutableStateOf(List(navItems.size) { 0f }) }
    
    // Track selections for directional animation
    val selectedIndex = navItems.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    var previousIndex by remember { mutableStateOf(selectedIndex) }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex != previousIndex) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) // Subtle vibration on move
            previousIndex = selectedIndex
        }
    }

    // ---- VISCOELASTIC PHYSICS ENGINE (The Liquid Stretch) ----
    val targetX = itemPositions.getOrElse(selectedIndex) { 0f }
    val previousTargetX = itemPositions.getOrElse(previousIndex) { 0f }
    val direction = targetX - previousTargetX

    // Leading edge travels fast towards the target
    val leadingEdge by animateFloatAsState(
        targetValue = targetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = if (direction >= 0) Spring.StiffnessMedium else Spring.StiffnessLow
        ),
        label = "leadingEdgeX"
    )

    // Trailing edge catches up slowly, creating the "Stretching Drop" effect
    val trailingEdge by animateFloatAsState(
        targetValue = targetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = if (direction >= 0) Spring.StiffnessLow else Spring.StiffnessMedium
        ),
        label = "trailingEdgeX"
    )

    // Math for the physical width of the indicator pill
    val baseWidthPx = with(density) { 64.dp.toPx() }
    val halfWidthPx = baseWidthPx / 2f
    
    val currentLeftPx = minOf(leadingEdge, trailingEdge) - halfWidthPx
    val currentRightPx = maxOf(leadingEdge, trailingEdge) + halfWidthPx
    val currentWidthPx = maxOf(baseWidthPx, currentRightPx - currentLeftPx)
    
    // Dynamic Lens Center for Refraction (Moves with the blob!)
    val lensCenterX = (currentLeftPx + currentRightPx) / 2f

    // ---- UI RENDERING ----
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp) // Floating effect
            .height(72.dp),
        contentAlignment = Alignment.Center
    ) {
        
        // ---------------------------------------------------------
        // LAYER 1: OPTICAL REFRACTION ENGINE (The Moving Distortion)
        // ---------------------------------------------------------
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(36.dp)) // Pill shape for the bar
                // Base Blur for the whole bar
                .cloudy(radius = 25)
                // THE MAGIC: Liquid Glass Refraction tracking the animated X position
                .liquidGlass(
                    lensCenter = Offset(lensCenterX, with(density) { 36.dp.toPx() }), // Centered Y, Dynamic X
                    lensSize = Size(400f, 400f), // Large enough to cover the blob
                    cornerRadius = 200f,
                    refraction = 0.35f, // Distorts background text/images
                    curve = 0.3f,
                    dispersion = 0.05f, // Slight RGB chromatic separation at edges
                    edge = 0.15f
                )
                // Dark tint to maintain contrast against any video/image
                .background(Color.Black.copy(alpha = 0.55f))
                // Ultra-thin glowing border
                .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(36.dp))
        )

        // ---------------------------------------------------------
        // LAYER 2: THE LIQUID CAPSULE INDICATOR
        // ---------------------------------------------------------
        // We only render this if the layout has been calculated
        if (itemPositions.isNotEmpty() && itemPositions[selectedIndex] > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp) // Keep indicator inside bounds
            ) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(currentLeftPx.roundToInt(), 0) }
                        .width(with(density) { currentWidthPx.toDp() })
                        .fillMaxHeight()
                        .padding(vertical = 12.dp)
                        // Pure bright white overlay for the active pill
                        .background(Color.White.copy(alpha = 0.25f), CircleShape)
                        // Subtle inner shadow/border for 3D feel
                        .border(0.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                )
            }
        }

        // ---------------------------------------------------------
        // LAYER 3: INTERACTIVE CRISP ICONS (No Blur applied here)
        // ---------------------------------------------------------
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = currentRoute == item.route
                
                // Animate Icon scale when selected
                val iconScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 0.9f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
                    label = "iconScale"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        // 1. Calculate X position dynamically for the physics engine
                        .onGloballyPositioned { coordinates ->
                            val newPositions = itemPositions.toMutableList()
                            // Get absolute center X of this layout cell
                            newPositions[index] = coordinates.positionInParent().x + (coordinates.size.width / 2f)
                            itemPositions = newPositions
                        }
                        // 2. Click Handler
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Custom interaction, no default android ripple
                        ) {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigate(item.route)
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Wrapper for scaling
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Shadow icon for depth
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = Color.Black.copy(alpha = 0.3f),
                            modifier = Modifier
                                .size((28 * iconScale).dp)
                                .offset(y = 2.dp)
                        )
                        // Foreground Icon
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            // Crystal white if selected, muted grey if not
                            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size((28 * iconScale).dp)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================================
// 3. UTILITY EXTENSIONS & HELPERS (For Enterprise Scaling)
// =========================================================================================

/**
 * Extension to safely get item from list avoiding IndexOutOfBounds.
 */
fun <T> List<T>.getOrElse(index: Int, defaultValue: () -> T): T {
    return if (index >= 0 && index < this.size) this[index] else defaultValue()
}

/**
 * Placeholder for future integration:
 * Generates a dynamic gradient brush based on the video frame behind the bar.
 */
@Composable
fun getDynamicGlassGradient(): Color {
    // Real implementation would extract Palette API colors from ExoPlayer surface.
    // Defaulting to a sleek dark tint.
    return Color(0xFF1A1C23) 
}

// END OF LIQUID BOTTOM BAR COMPONENT
