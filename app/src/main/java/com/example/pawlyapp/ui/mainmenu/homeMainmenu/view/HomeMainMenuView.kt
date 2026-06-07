package com.example.pawlyapp.ui.mainmenu.homeMainmenu.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawlyapp.ui.mainmenu.homeMainmenu.viewmodel.HomeMainMenuViewModel

@Composable
fun HomeMainMenuView(
    homeViewModel: HomeMainMenuViewModel = viewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Main Menu")
    }
}