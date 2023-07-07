package com.hackvem.games

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.hackvem.games.screen.roulette.RouletteGameScreen
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

internal enum class GameType(
    val id: Int = -1,
    val screen: @Composable () -> Unit
) {
    IDLE(screen = {}),
    ROULETTE(id = R.string.game_roulette, { RouletteGameScreen() })
}

internal val LocalGameControllerProvider = staticCompositionLocalOf { GameController() }

internal class GameController {
    val selectedGameState = MutableStateFlow(GameType.IDLE)

    fun selectGame(game: GameType) = CoroutineScope(Dispatchers.Main).launch {
        delay(300)
        selectedGameState.value = game
    }
}