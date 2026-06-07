package com.example.pawlyapp.ui.personalinformation.homePersonalinformation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawlyapp.ui.personalinformation.homePersonalinformation.viewmodel.HomePersonalinformationViewModel

@Composable
fun HomePersonalinformationView(
    homeViewModel: HomePersonalinformationViewModel = viewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Personal Information")
    }
}