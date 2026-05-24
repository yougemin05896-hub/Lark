package com.example.presentation.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asComposeRenderEffect
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build

class LayerBackdrop

@Composable
fun rememberLayerBackdrop(): LayerBackdrop {
    return remember { LayerBackdrop() }
}

fun Modifier.layerBackdrop(backdrop: LayerBackdrop): Modifier = this

class BackdropEffectsScope {
    var blurRadius: Float = 0f
    fun vibrancy() {}
    fun blur(radius: Float) { blurRadius = radius }
    fun lens(x: Float, y: Float) {}
}

fun Modifier.drawBackdrop(
    backdrop: LayerBackdrop,
    shape: () -> Shape,
    effects: BackdropEffectsScope.() -> Unit,
    onDrawSurface: DrawScope.() -> Unit
): Modifier = this.then(
    Modifier.graphicsLayer {
        val scope = BackdropEffectsScope().apply(effects)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && scope.blurRadius > 0f) {
            renderEffect = RenderEffect.createBlurEffect(scope.blurRadius, scope.blurRadius, Shader.TileMode.MIRROR).asComposeRenderEffect()
        }
        clip = true
        this.shape = shape()
    }.drawWithContent {
        drawContent()
        onDrawSurface()
    }
)
