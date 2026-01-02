package com.example.extra_ordinary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.extra_ordinary.ui.screens.HomeScreen
import com.example.extra_ordinary.ui.theme.ExtraordinaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExtraordinaryTheme {
                HomeScreen()
            }
        }
    }
}
