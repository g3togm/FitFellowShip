package com.example.fellowshiprunner.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.fellowshiprunner.R

/**
 * Full-screen dark background with the Mordor image and a darkening overlay.
 * No changes needed here — the button/nav fix is handled by Scaffold innerPadding in MainView.
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