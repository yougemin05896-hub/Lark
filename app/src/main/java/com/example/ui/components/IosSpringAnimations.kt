package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

/**
 * Apple-style bouncy physics constants
 */
object IosSpringAnimations {
    val bounciness = spring<Float>(
        dampingRatio = 0.6f, // Somewhat bouncy
        stiffness = Spring.StiffnessMediumLow
    )
    
    val defaultSpring = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = Spring.StiffnessMedium
    )
}
