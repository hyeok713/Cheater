package com.hackvem.games.screen.roulette

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hackvem.games.GameType
import com.hackvem.games.LocalGameControllerProvider
import com.hackvem.games.R
import kotlinx.coroutines.launch

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

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false),
    )
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.game_roulette)) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back Button",
                        modifier = Modifier.clickable { gameController.selectGame(GameType.IDLE) }
                    )
                },
            )
        },
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.info_roulette))
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        scope.launch { scaffoldState.bottomSheetState.hide() }
                    }
                ) {
                    Text("I GOT IT")
                }
            }
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            RouletteGameNavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController
            )

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                containerColor = Color.White,
                onClick = {
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info Icon"
                )
            }
        }
    }
}

@Composable
fun RouletteGameNavHost(modifier: Modifier, navController: NavHostController) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = RouletteRoute.Game.route
    ) {
        composable(route = RouletteRoute.Game.route) { RouletteGameView() }
    }
}
