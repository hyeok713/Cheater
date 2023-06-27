package com.hackvem.games.screen.roulette

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hackvem.games.GameType
import com.hackvem.games.LocalGameControllerProvider
import com.hackvem.games.R

sealed class RouletteRoute(val route: String) {
    object Setting : RouletteRoute("setting")
    object Game : RouletteRoute("game")
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RouletteGameScreen() {
    val gameController = LocalGameControllerProvider.current
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.game_roulette)) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back Button",
                        modifier = Modifier.clickable { gameController.selectGame(GameType.IDLE) }
                    )
                }
            )
        },
    ) {
        // 1. add candidates as wish
        RouletteGameNavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController
        )
    }
}


@Composable
fun RouletteGameNavHost(modifier: Modifier, navController: NavHostController) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = RouletteRoute.Game.route
    ) {
        composable(route = RouletteRoute.Setting.route) { RouletteCandidateView(navController) }
        composable(route = RouletteRoute.Game.route) { RouletteGameView() }
    }
}
