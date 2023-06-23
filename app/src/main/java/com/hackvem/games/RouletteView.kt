package com.hackvem.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RouletteWheel() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition()

        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 3060f,    // 타겟을 설정 해야함
            animationSpec = infiniteRepeatable(
                animation = tween<Float>(
                    durationMillis = 5000,
                    easing = FastOutSlowInEasing,
                ),
            )
        )

        Canvas(modifier = Modifier.fillMaxSize(0.9f)) {
            rotate(rotation) {
                drawRouletteWheel(8)
            }
        }
    }
}


private const val ROUND_ANGLE = 360f
private fun DrawScope.drawRouletteWheel(capability: Int = 3) {
    val wheelRadius = size.minDimension / 2
    val centerX = size.width / 2
    val centerY = size.height / 2
    val strokeWidth = 4.dp.toPx()

    val angle = ROUND_ANGLE / capability

    val colorList = listOf(
        Color.Red,
        Color.Magenta,
        Color.Yellow,
        Color.Green,
        Color.Blue,
        Color.Cyan,
        Color.LightGray,
        Color.White
    ).map { it.copy(alpha = 0.9f) }

    val texts = listOf("A","B","C","D","E","F","G","H")

    // Draw the outer circle
    drawCircle(
        color = Color.Black,
        radius = wheelRadius,
        center = Offset(centerX, centerY),
        style = Stroke(strokeWidth)
    )

    // Draw text on each half
    val textSize = 24.sp.toPx()
    val textOffset = 0.4f // Offset to position the text within the halves
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textAlign = android.graphics.Paint.Align.CENTER
        this.textSize = textSize
    }

    repeat(capability) {
        // Draw the first half of the circle
        drawArc(
            color = colorList[it],
            startAngle = angle * it,
            sweepAngle = angle,
            useCenter = true,
            topLeft = Offset(centerX - wheelRadius, centerY - wheelRadius),
            size = Size(wheelRadius * 2, wheelRadius * 2),
            style = Fill
        )

        drawContext.canvas.nativeCanvas.drawText(
            texts[it],
            centerX,
            centerY,
            textPaint
        )
    }
}

@Preview
@Composable
fun PreviewRouletteWheel() {
    RouletteWheel()
}
