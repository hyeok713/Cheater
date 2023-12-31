package com.hackvem.games

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackvem.games.ui.theme.ext.noRippleClickable
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BackHandler(false) {}
            CompositionLocalProvider(LocalGameControllerProvider provides GameController()) {
                GamesScreen()
            }
        }
    }
}

/**
 * GamesScreen
 * game-selectable screen
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Preview
@Composable
fun GamesScreen() {
    val gameController = LocalGameControllerProvider.current
    val gameState = gameController.selectedGameState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            GameBox(GameType.ROULETTE) { gameController.selectGame(it) }
            /* TODO: additional games.. */
        }

        if (gameState.value != GameType.IDLE) {
            gameState.value.screen.invoke()
        }
    }
}

@Composable
private fun GameBox(type: GameType, onClick: (GameType) -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val animatedProgress: Float by animateFloatAsState(
        targetValue = if (pressed) 1.2f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing,
        ),
    )
    Box(
        modifier = Modifier
            .scale(animatedProgress)
            .background(color = Color.Transparent, shape = RoundedCornerShape(36.dp))
            .border(width = 4.dp, color = Color.Black, shape = RoundedCornerShape(36.dp))
            .noRippleClickable(
                onPress = { pressed = it },
                onClick = { onClick(type) }
            ),
    ) {
        Text(
            text = stringResource(type.id),
            fontSize = 38.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 24.dp, 8.dp)
        )
    }
}

