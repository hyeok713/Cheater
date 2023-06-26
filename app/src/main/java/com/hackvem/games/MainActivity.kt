package com.hackvem.games

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * MainActivity
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
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
fun GameBox(type: GameType, onClick: (GameType) -> Unit) {
    Box(
        modifier = Modifier
            .background(color = Color.Transparent, shape = RoundedCornerShape(36.dp))
            .border(width = 4.dp, color = Color.Black, shape = RoundedCornerShape(36.dp))
            .clickable { onClick(type) },
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

@Composable
fun RouletteGame() {
    val navController = rememberNavController()

    RouletteWheel()
}

enum class GameType(val id: Int = -1, val screen: @Composable () -> Unit) {
    IDLE(screen = {}),
    ROULETTE(id = R.string.game_roulette, { RouletteGame() })
}

internal val LocalGameControllerProvider = staticCompositionLocalOf { GameController() }

internal class GameController {
    val selectedGameState = MutableStateFlow(GameType.IDLE)

    fun selectGame(game: GameType) {
        selectedGameState.value = game
    }
}