@file:OptIn(ExperimentalMaterial3Api::class)

package com.hackvem.games.screen.roulette

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun RouletteCandidateView() {
    // default number of candidate is 4
    var maxCandidates by remember { mutableStateOf(4) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(maxCandidates) {
                Candidate()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { maxCandidates-- }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Icon")
                }

                IconButton(
                    onClick = { maxCandidates++ }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Icon")
                }
            }
        }
    }
}

@Preview
@Composable
fun Candidate() {
    var text by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .background(color = Color.Transparent, shape = RoundedCornerShape(36.dp))
            .border(width = 4.dp, color = Color.Black, shape = RoundedCornerShape(36.dp)),
    ) {
        TextField(
            modifier = Modifier.padding(horizontal = 24.dp, 8.dp),
            value = text,
            onValueChange = {
                text = it

            },
            singleLine = true,
            maxLines = 1,
            textStyle = TextStyle.Default.copy(
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

