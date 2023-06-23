package com.hackvem.games

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * MainActivity
 * 사다리 게임 설정 -> 진행 -> 결과 화면
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RouletteWheel()
        }
    }
}