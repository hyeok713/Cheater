package com.hackvem.games

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.lang.Math.cos
import java.lang.Math.sin
import java.util.*

private const val ROUND_ANGLE = 360f
private val COLOR_LIST = listOf(
    Color.Red.copy(blue = 0.1f, green = 0.2f),
    Color.Magenta,
    Color.Yellow,
    Color.Green,
    Color.Blue.copy(green = 0.5f),
    Color.Cyan,
    Color.Yellow.copy(red = 0.1f, blue = 0.4f, green = 0.6f),
    Color.Red.copy(red = 0.6f, blue = 0.3f)
)

@Composable
fun RouletteWheel(
    userList: List<String> = listOf("1", "2", "3", "4"),
    targetUser: Int = -1,
) {
    // 20 turn + random / 20 turn + target angle
    var targetValue: Float = getTargetAngle(targetUser, userList.size)

    // set colors randomly
    val colors = COLOR_LIST.shuffled().subList(0, userList.size)

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var isStarted by remember { mutableStateOf(false) }
        var isFinished by remember { mutableStateOf(false) }

        val animatedProgress by animateFloatAsState(
            targetValue = if (!isStarted) 0f else targetValue,
            animationSpec = tween(
                durationMillis = if (isStarted) 1000 else 1000,
                easing = FastOutSlowInEasing,
            ),
            finishedListener = {
                /* TODO:  */
                isFinished = true
            }
        )

        Canvas(
            modifier = Modifier.fillMaxSize(0.9f)
        ) {
            rotate(animatedProgress) {
                drawRouletteWheel(
                    userList,
                    colors
                )
            }
        }

        when (isStarted) {
            true -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow),
                    contentDescription = "Arrow Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .offset(0.dp, -(maxWidth / 2))
                )
            }

            false -> {
                IconButton(
                    onClick = { isStarted = true },
                    modifier = Modifier.size(128.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_start),
                        contentDescription = "Start Button",
                    )
                }
            }
        }

        when (isFinished) {
            true -> {
                IconButton(
                    onClick = {
                        isStarted = false
                        isFinished = false
                        targetValue = getTargetAngle(targetUser, userList.size)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .offset(0.dp, maxWidth / 2 + 20.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow),
                        contentDescription = "Arrow Icon",
                    )
                }
            }

            false -> {

            }
        }
    }
}

/**
 * drawRouletteWheel
 * @param itemList List<String>
 *
 * requires string item list to put text each surface of parts
 */
private fun DrawScope.drawRouletteWheel(
    itemList: List<String>,
    colors: List<Color>,
) {
    val wheelRadius = size.minDimension / 2
    val centerX = size.width / 2
    val centerY = size.height / 2
    val strokeWidth = 7.dp.toPx()

    val angle = ROUND_ANGLE / itemList.size

    // Draw the outer circle
    drawCircle(
        color = Color.Black,
        radius = wheelRadius,
        center = Offset(centerX, centerY),
        style = Stroke(strokeWidth)
    )

    // Draw arcs and text on each split part
    val textSize = (if (itemList.any { it.length >= 6 }) 16 else 20).sp.toPx()
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textAlign = android.graphics.Paint.Align.CENTER
        this.textSize = textSize
    }

    repeat(itemList.size) { index ->
        val startAngle = -(angle * index) - (90 + angle)

        // Draw the split part
        drawArc(
            color = colors[index],
            startAngle = startAngle,
            sweepAngle = angle,
            useCenter = true,
            topLeft = Offset(centerX - wheelRadius, centerY - wheelRadius),
            size = Size(wheelRadius * 2, wheelRadius * 2),
            style = Fill
        )

        // Calculate the position of the text
        val textAngle = startAngle + angle / 2
        val textX =
            centerX + (wheelRadius / 1.5f - textSize / 2) * cos(Math.toRadians(textAngle.toDouble())).toFloat()
        val textY =
            centerY + (wheelRadius / 1.5f - textSize / 2) * sin(Math.toRadians(textAngle.toDouble())).toFloat()

        // Draw the text
        drawContext.canvas.nativeCanvas.drawText(
            itemList[index],
            textX,
            textY,
            textPaint
        )
    }
}

@Preview
@Composable
fun PreviewRouletteWheel() {
    RouletteWheel()
}

/**
 * getTargetAngle
 * @param targetIndex Int
 *
 * get target angle randomly
 * if targetIndex is positive digit,
 * specify angle for target. otherwise get random angle
 */
private fun getTargetAngle(targetIndex: Int, size: Int): Float {
    val result = if (targetIndex + 1 > 0) {
        val angle = ROUND_ANGLE.toInt() / size
        val maxAngle = (targetIndex + 1) * angle
        rand(maxAngle, maxAngle - angle + 1)
    } else {
        rand(ROUND_ANGLE.toInt())
    }.toFloat()

    println("result: $result")
    return result
}

/**
 * random
 * @param bound Int
 * @param from Int
 */
fun rand(to: Int, from: Int = 0) : Int = Random().nextInt(to - from) + from