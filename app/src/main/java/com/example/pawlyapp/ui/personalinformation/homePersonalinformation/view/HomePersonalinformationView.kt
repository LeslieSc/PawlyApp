package com.example.pawlyapp.ui.personalinformation.homePersonalinformation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawlyapp.ui.personalinformation.homePersonalinformation.viewmodel.HomePersonalinformationViewModel
@Composable
fun HomePersonalinformationView(
    onLogout: () -> Unit,
    homeViewModel: HomePersonalinformationViewModel = viewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Personal Information")

        Button(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ){
            Text("Cerrar Sesión")
        }
    }
}