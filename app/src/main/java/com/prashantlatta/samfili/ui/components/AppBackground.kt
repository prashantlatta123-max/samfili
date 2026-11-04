package com.prashantlatta.samfili.ui.components

import android.net.Uri
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun AppBackground(
    imageUri: String?,
    opacity: Float,
    isBlurred: Boolean,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        if (!imageUri.isNullOrEmpty()) {
            val blurModifier = if (isBlurred && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Modifier.blur(22.dp)
            } else {
                Modifier
            }

            AsyncImage(
                model = Uri.parse(imageUri),
                contentDescription = "Custom Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (isBlurred) (opacity * 0.7f) else opacity)
                    .then(blurModifier)
            )

            if (isBlurred) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )
            }
        }

        content()
    }
}
