package com.example.pawlyapp.ui.mainmenu.homeMainmenu.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawlyapp.ui.mainmenu.homeMainmenu.viewmodel.HomeMainMenuViewModel

@Composable
fun HomeMainMenuView(
    onNavigateToFirstApiRequest: () -> Unit = {},
    homeViewModel: HomeMainMenuViewModel = viewModel()
) {


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Main Menu")

        Button(onClick = onNavigateToFirstApiRequest) {
            Text("Razas")
        }
    }
}
