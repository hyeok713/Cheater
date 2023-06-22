package com.hackvem.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RouletteWheel() {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRouletteWheel()
        }
    }
}

private fun DrawScope.drawRouletteWheel() {
    val wheelRadius = size.minDimension / 2
    val centerX = size.width / 2
    val centerY = size.height / 2
    val strokeWidth = 4.dp.toPx()

    // Draw the outer circle
    drawCircle(
        color = Color.Black,
        radius = wheelRadius,
        center = Offset(centerX, centerY),
        style = Stroke(strokeWidth)
    )

    // Draw the first half of the circle
    drawArc(
        color = Color.Red,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - wheelRadius, centerY - wheelRadius),
        size = Size(wheelRadius * 2, wheelRadius * 2),
        style = Fill
    )

    // Draw the second half of the circle
    drawArc(
        color = Color.Blue,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - wheelRadius, centerY - wheelRadius),
        size = Size(wheelRadius * 2, wheelRadius * 2),
        style = Fill
    )

    // Draw text on each half
    val textSize = 24.sp.toPx()
    val textOffset = 0.4f // Offset to position the text within the halves
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        textAlign = android.graphics.Paint.Align.CENTER
        this.textSize = textSize
    }

    val text1 = "First Half"
    val text2 = "Second Half"

    drawContext.canvas.nativeCanvas.drawText(
        text1,
        centerX,
        centerY - textOffset * wheelRadius,
        textPaint
    )

    drawContext.canvas.nativeCanvas.drawText(
        text2,
        centerX,
        centerY + textOffset * wheelRadius + textSize,
        textPaint
    )
}

@Preview
@Composable
fun PreviewRouletteWheel() {
    RouletteWheel()
}
