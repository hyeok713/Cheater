package com.hackvem.games.screen.roulette

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hackvem.games.LocalGameControllerProvider
import com.hackvem.games.R
import java.util.*

private const val ROUND_ANGLE = 360f
private const val MIN_CANDIDATE = 2
private const val MAX_CANDIDATE = 8

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


@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RouletteGameView() {
    val viewModel: RouletteViewModel = hiltViewModel()
    val gameController = LocalGameControllerProvider.current

    val userList by remember { mutableStateOf(mutableListOf("", "", "", "")) }

    BackHandler(false) {}

    // 20 turn + random / 20 turn + target angle
    var targetIndex by remember { mutableStateOf(-1) }  // Manipulated Index (target)
    var targetValue: Float = getTargetAngle(targetIndex, userList.size)
    var resultTarget by remember { mutableStateOf("") }

    var isStarted by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var selectedIndex by remember { mutableStateOf(-1) } // Selected Index for label setting

        val animatedProgress by animateFloatAsState(
            targetValue = if (!isStarted) 0f else targetValue,
            animationSpec = tween(
                durationMillis = if (isStarted) 3000 else 1000,
                easing = FastOutSlowInEasing,
            ),
            finishedListener = { isFinished = true }
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .pointerInput(Unit) {
                    // returns when roulette game started or finished
                    // this action will only work on the state of 'ready'
                    if (isStarted || isFinished) return@pointerInput

                    detectTapGestures(
                        onDoubleTap = { doubleTapGesture ->
                            targetIndex = viewModel.getIndexFromAngle(
                                doubleTapGesture,
                                size,
                                ROUND_ANGLE / userList.size
                            )
                            println("targetIndex: $targetIndex")
                        },
                        onTap = { tapGesture ->
                            selectedIndex = viewModel.getIndexFromAngle(
                                tapGesture,
                                size,
                                ROUND_ANGLE / userList.size
                            )
                        })
                }
        ) {
            /*
             * When start button clicked, animatedProgress value (which is float)
             * will be animating to targetValue, turns out rotate RouletteWheel
             */
            rotate(animatedProgress) {
                drawRouletteWheel(userList)
            }
        }

        when (isStarted) {
            true -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow),
                    contentDescription = "Arrow Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .offset(0.dp, -(maxWidth / 2) + 15.dp),
                    tint = Color.Unspecified
                )
            }

            false -> {
                Box(
                    modifier = Modifier
                        .offset(0.dp, maxWidth / 2 + 20.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(32.dp)
                        )
                        .clickable {
                            isStarted = true
                            isFinished = false
                        }

                ) {
                    Text(
                        text = "START",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(0.dp, -(maxWidth / 2) + 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (userList.size > MIN_CANDIDATE) {
                        IconButton(onClick = { userList.removeLast() }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete Icon")
                        }
                    }

                    if (userList.size < MAX_CANDIDATE) {
                        IconButton(onClick = { userList.add("") }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Icon")
                        }
                    }
                }
            }
        }

        when (isFinished) {
            true -> {
                if (isStarted) {
                    resultTarget =
                        userList[((targetValue - 3600) / (ROUND_ANGLE / userList.size)).toInt()]
                    // Let 'Restart button' visible
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.Black.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(32.dp)
                            )
                            .clickable {
                                isStarted = false
                                isFinished = false
                                targetValue = getTargetAngle(targetIndex, userList.size)
                                resultTarget = ""
                            }
                    ) {
                        Text(
                            text = stringResource(R.string.restart),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            false -> {}
        }

        /* Display Result target name after finished game (roulette turning) */
        AnimatedContent(
            targetState = resultTarget,
            transitionSpec = {
                slideInVertically { it } with slideOutVertically { -it }
            },
            modifier = Modifier.offset(0.dp, maxWidth / 2 + 20.dp)
        ) {
            Text(
                text = it,
                fontSize = 30.sp,
                color = Color.Black
            )
        }

        /* Display input box layer when target selected only in the state of 'ready' */
        if (selectedIndex >= 0) {
            // only if index is positive
            InputBoxLayer(
                targetIndex = selectedIndex,
                originText = userList[selectedIndex],
                color = COLOR_LIST[selectedIndex]
            ) { index, text ->
                userList[index] = text
                selectedIndex = -1  // remove this layer
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
            color = COLOR_LIST[index],
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
            centerX + (wheelRadius / 1.5f - textSize / 2) * kotlin.math.cos(Math.toRadians(textAngle.toDouble()))
                .toFloat()
        val textY =
            centerY + (wheelRadius / 1.5f - textSize / 2) * kotlin.math.sin(Math.toRadians(textAngle.toDouble()))
                .toFloat()

        // Draw the text
        drawContext.canvas.nativeCanvas.drawText(
            itemList[index],
            textX,
            textY,
            textPaint
        )
    }
}

/**
 * InputBoxLayer
 * TextField which enables target setting on selected spot
 */
@Preview
@Composable
fun InputBoxLayer(
    targetIndex: Int = 0,
    originText: String = "",
    color: Color = Color.Yellow,
    out: (Int, String) -> Unit = { _, _ -> }
) {
    val focusRequester = FocusRequester()

    var text by remember { mutableStateOf(originText) }
    val infiniteTransition = rememberInfiniteTransition()

    val colorAnim by infiniteTransition.animateColor(
        initialValue = color,
        targetValue = color.copy(alpha = 0.2f),
        animationSpec = infiniteRepeatable(
            // Linearly interpolate between initialValue and targetValue every 1000ms.
            animation = tween(1000, easing = LinearEasing),
            // Once [TargetValue] is reached, starts the next iteration in reverse (i.e. from
            // TargetValue to InitialValue). Then again from InitialValue to TargetValue. This
            // [RepeatMode] ensures that the animation value is *always continuous*.
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.enter_target),
                fontSize = 16.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            BasicTextField(
                modifier = Modifier
                    .border(
                        border = BorderStroke(
                            brush = Brush.horizontalGradient(
                                0.1f to colorAnim,
                                1f to colorAnim,
                                tileMode = TileMode.Mirror
                            ), width = 2.dp
                        ), shape = RoundedCornerShape(45.dp)
                    )
                    .focusRequester(focusRequester),
                value = text,
                cursorBrush = SolidColor(color),
                onValueChange = { str ->
                    text = str
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    color = color
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        innerTextField()
                    }

                },
                singleLine = true,
                maxLines = 1,
                keyboardActions = KeyboardActions {
                    /* do when action */
                    out(targetIndex, text)
                },
            )
        }
    }

    SideEffect {
        focusRequester.requestFocus()
    }
}


@Preview
@Composable
fun PreviewRouletteWheel() {
    RouletteGameView()
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
    val result = if (targetIndex >= 0) {
        val angle = ROUND_ANGLE.toInt() / size
        val maxAngle = (targetIndex + 1) * angle
        rand(maxAngle, maxAngle - angle + 1)
    } else {
        rand(ROUND_ANGLE.toInt())
    } + 3600.toFloat()

    return result
}

/**
 * random
 * @param to Int
 * @param from Int
 */
fun rand(to: Int, from: Int = 0): Int = Random().nextInt(to - from) + from