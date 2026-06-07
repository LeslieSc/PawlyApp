package com.example.pawlyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pawlyapp.ui.navigation.AppNavigation
import com.example.pawlyapp.ui.theme.PawlyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PawlyAppTheme {
                AppNavigation()
            }
        }
    }
}
