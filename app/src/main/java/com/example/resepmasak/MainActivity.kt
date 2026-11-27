package com.example.resepmasak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.resepmasak.ui.navigation.AppNavigation
import com.example.resepmasak.ui.theme.ResepMasakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResepMasakTheme {
                AppNavigation()
            }
        }
    }
}