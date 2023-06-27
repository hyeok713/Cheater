package com.hackvem.games.screen.roulette

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.hackvem.games.R

@SuppressLint("MutableCollectionMutableState")
@Composable
fun RouletteCandidateView(navController: NavHostController) {
    // default number of candidate is 4
    var maxCandidates by remember { mutableStateOf(4) }
    val candidates by remember { mutableStateOf(mutableListOf("", "", "", "", "", "", "", "")) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(maxCandidates) { index ->
                Candidate { text -> candidates[index] = text }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (maxCandidates > 2) {
                    IconButton(onClick = { maxCandidates-- }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete Icon")
                    }
                }

                if (maxCandidates < 8) {
                    IconButton(onClick = { maxCandidates++ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Icon")
                    }
                }
            }
        }

        IconButton(
            onClick = {
                navController.navigate(route = RouletteRoute.Game.route) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = "Next Icon"
            )
        }
    }
}

@Composable
fun Candidate(out: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .background(color = Color.Transparent, shape = RoundedCornerShape(36.dp))
            .border(width = 4.dp, color = Color.Black, shape = RoundedCornerShape(36.dp)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
            value = text,
            onValueChange = {
                text = it
                out(it)
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            ),
            decorationBox = { innerTextField ->
                innerTextField()
            },
            singleLine = true,
            maxLines = 1,
        )
    }

    Spacer(modifier = Modifier.height(4.dp))
}

