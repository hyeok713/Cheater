package com.hackvem.games.screen.roulette

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.hackvem.games.LocalGameControllerProvider
import com.hackvem.games.R
import com.hackvem.games.screen.roulette.RouletteViewModel.Companion.COLOR_LIST
import com.hackvem.games.screen.roulette.RouletteViewModel.Companion.MAX_CANDIDATE
import com.hackvem.games.screen.roulette.RouletteViewModel.Companion.MIN_CANDIDATE
import com.hackvem.games.screen.roulette.RouletteViewModel.Companion.ROUND_ANGLE
import com.hackvem.games.ui.theme.ext.noRippleClickable
import java.util.*

private const val TIME_RESET = 1000
private const val TIME_RUNNING = 5000

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RouletteGameView() {
    val viewModel: RouletteViewModel = hiltViewModel()
    val gameController = LocalGameControllerProvider.current

    val targetList = remember { mutableStateListOf("", "", "", "") }

    var manipulatedTargetIndex by remember { mutableStateOf(-1) }  // Manipulated Index (target)
    var targetValue = viewModel.getTargetAngle(
        manipulatedTargetIndex,
        ROUND_ANGLE.toInt() / targetList.size
    )

    var resultTarget by remember { mutableStateOf("") }

    // states of game
    var gameStatus by remember { mutableStateOf(GameStatus.READY) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var selectedIndex by remember { mutableStateOf(-1) } // Selected Index for label setting

        val animatedProgress: Float by animateFloatAsState(
            targetValue = if (gameStatus == GameStatus.READY) 0f else targetValue,
            animationSpec = tween(
                durationMillis = if (gameStatus != GameStatus.READY) TIME_RUNNING else TIME_RESET,
                easing = FastOutSlowInEasing,
            ),
            finishedListener = {
                // it generates by any direction. so distinguishing direction required
                gameStatus =
                    if (gameStatus == GameStatus.READY) GameStatus.READY else GameStatus.FINISHED
            }
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .pointerInput(Unit) {
                    // returns when roulette game started or finished
                    // this action will only work on the state of 'ready'
                    if (gameStatus == GameStatus.READY) {
                        detectTapGestures(
                            onDoubleTap = { doubleTapGesture ->
                                manipulatedTargetIndex = viewModel.getIndexFromAngle(
                                    doubleTapGesture,
                                    size,
                                    ROUND_ANGLE / targetList.size
                                )
                            },
                            onTap = { tapGesture ->
                                selectedIndex = viewModel.getIndexFromAngle(
                                    tapGesture,
                                    size,
                                    ROUND_ANGLE / targetList.size
                                )
                            }
                        )
                    }
                }
        ) {
            /*
             * When start button clicked, animatedProgress value (which is float)
             * will be animating to targetValue, turns out rotate RouletteWheel
             */
            rotate(animatedProgress) {
                drawRouletteWheel(targetList)
            }
        }

        when (gameStatus) {
            GameStatus.READY -> {
                val minusBtnVisibility by remember {
                    derivedStateOf { targetList.size > MIN_CANDIDATE }
                }

                val plusBtnVisibility by remember {
                    derivedStateOf { targetList.size < MAX_CANDIDATE }
                }

                Row(modifier = Modifier.offset(0.dp, maxWidth / 2 + 30.dp)) {
                    AdjustableButton(iconId = R.drawable.ic_minus, minusBtnVisibility) { targetList.removeLast() }
                    Spacer(modifier = Modifier.width(8.dp))
                    StartButton { gameStatus = GameStatus.STARTED }
                    Spacer(modifier = Modifier.width(8.dp))
                    AdjustableButton(iconId = R.drawable.ic_add, plusBtnVisibility) { targetList.add("") }
                }
            }

            else -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow),
                    contentDescription = "Arrow Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .offset(0.dp, -(maxWidth / 2) + 15.dp),
                    tint = Color.Unspecified
                )

                if (gameStatus == GameStatus.FINISHED) {
                    resultTarget =
                        targetList[viewModel.getResultIndex(
                            targetValue,
                            ROUND_ANGLE / targetList.size
                        )]
                    // Let 'Restart button' visible
                    StartButton(id = R.string.restart) {
                        gameStatus = GameStatus.READY
                        resultTarget = ""
                        manipulatedTargetIndex = -1
                        targetValue = viewModel.getTargetAngle(
                            manipulatedTargetIndex,
                            ROUND_ANGLE.toInt() / targetList.size
                        )
                    }
                }
            }
        }

        /* Display Result target name after finished game (roulette turning) */
        AnimatedContent(
            targetState = resultTarget,
            transitionSpec = {
                slideInVertically { it } with slideOutVertically { -it }
            },
            modifier = Modifier.offset(0.dp, maxWidth / 2 + 30.dp)
        ) {
            Text(
                text = it,
                fontSize = 30.sp,
                color = Color.Black
            )
        }

        /* Display input box layer when target selected only in the state of 'READY' */
        // only if index is positive
        if (selectedIndex >= 0 && gameStatus == GameStatus.READY) {
            InputBoxLayer(
                targetIndex = selectedIndex,
                originText = targetList[selectedIndex],
                color = COLOR_LIST[selectedIndex]
            ) { index, text ->
                targetList[index] = text
                selectedIndex = -1  // remove this layer
            }
        } else {
            selectedIndex = -1
        }
    }
}


@Composable
private fun StartButton(id: Int = R.string.start, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .background(
                color = Color.Black.copy(alpha = if (!pressed) 0.8f else 1f),
                shape = RoundedCornerShape(32.dp)
            )
            .border(
                width = 2.dp,
                color = Color.White,
                shape = RoundedCornerShape(32.dp)
            )
            .noRippleClickable(
                onPress = {
                    pressed = it
                },
                onClick = { onClick() }
            )
    ) {
        Text(
            text = stringResource(id = id),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AdjustableButton(iconId: Int, isVisible: Boolean, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    LaunchedEffect(isVisible) {
        pressed = false
    }
    Icon(
        modifier = Modifier
            .background(
                color = if (pressed) Color.Black else Color.White,
                shape = CircleShape
            )
            .size(48.dp)
            .padding(8.dp)
            .alpha(if (!isVisible) 0f else 1f)
            .noRippleClickable(
                onPress = { if (isVisible) pressed = it },
                onClick = { if (isVisible) onClick() }
            ),
        painter = painterResource(id = iconId),
        tint = if (pressed) Color.White else Color.Black,
        contentDescription = "Delete/Add Icon"
    )
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
    out: (Int, String) -> Unit = { _, _ -> },
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
            .background(color = Color.Black.copy(alpha = 0.9f))
            .clickable(
                // ensure that the top layer doesn't intercept any click events and is completely transparent to touch events.
                enabled = true,
                indication = null,
                interactionSource = MutableInteractionSource()
            ) {},
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

    // this let text field be focused
    SideEffect {
        focusRequester.requestFocus()
    }
}

enum class GameStatus {
    READY,
    STARTED,
    FINISHED
}

@Preview
@Composable
fun PreviewRouletteWheel() {
    RouletteGameView()
}