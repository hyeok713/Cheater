package com.hackvem.games

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp



@Preview
@Composable
fun ZoomInOnOffsetScreen() {
    var translate by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { centroid, _, zoom, _ ->
                    // Calculate the offset of the centroid from the top-left corner of the screen
                    val screenOffset = Offset(centroid.x, centroid.y)

                    // Calculate the offset of the centroid from the center of the screen
                    val centerOffset = Offset((size.width / 2).toFloat(),
                        (size.height / 2).toFloat()
                    ) - screenOffset

                    // Calculate the scale factor for zooming in
                    val scaleFactor = zoom + 1f

                    // Apply the transformations
                    scale *= scaleFactor
                    translate += centerOffset * scale
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(45f)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        val down = awaitFirstDown()
                        translate = Offset.Zero
                        scale = 1f
                    }
                }
        ) {
            Text(
                text = "Zoom In On Offset",
                modifier = Modifier
                    .offset(100.dp, 100.dp)
                    .scale(scale)
                    .background(Color.Red)
                    .padding(16.dp)
            )
        }
    }
}