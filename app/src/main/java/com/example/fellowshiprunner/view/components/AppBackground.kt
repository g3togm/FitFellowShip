package com.example.fellowshiprunner.view.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

// fuer vulkan-background
import com.example.fellowshiprunner.R

/**
 * Full-screen dark background.
 *
 * Currently uses a volcanic radial gradient overlay.
 * To switch to a real background image: set [imageRes] and the
 * gradient fallback becomes unused and can be removed.
 *
 * [gradientHeightFraction] controls how far down the gradient extends (0f–1f).
 */
@Composable
fun AppBackground(
    gradientHeightFraction: Float = 0.45f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0705))
    ) {
        Image(
            painter = painterResource(R.drawable.mordor),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.55f
        )
        // Dark overlay so text stays readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0705).copy(alpha = 0.65f))
        )
        content()
    }
}
