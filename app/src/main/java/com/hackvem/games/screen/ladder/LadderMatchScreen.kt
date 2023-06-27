package com.hackvem.games.screen.ladder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hackvem.games.ui.theme.ext.MaxSizeBox

/**
 * 사다리에 인원 배정
 *
 * 1. 몇명이서?
 * 2. 사다리 제작
 * 3. 사다리 배정
 */
@Composable
fun LadderMatchScreen() {
    MemberCountView()
}

/**
 * 멤버 설정
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MemberCountView() {
    var text by remember { mutableStateOf("")}
    MaxSizeBox(
        modifier = Modifier.background(color= Color.White)
    ) {
        Column (modifier = Modifier.width(IntrinsicSize.Max)){
            Text(text = "몇명이서?")

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(value = text, onValueChange = {
                text = it
            })

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("확인")
            }
        }
    }
}