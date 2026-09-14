package com.example.pawlyapp.ui.auth.view

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawlyapp.ui.auth.viewmodel.LoginViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pawlyapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun LoginScreenView(onLoginSuccess: () -> Unit,
                    viewModel: LoginViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    val backgroundColor = Color(0xFFF7F1EA)
    val cardColor = Color.White
    val primaryBrown = Color(0xFF8B6B4F)
    val darkBrown = Color(0xFF4E342E)
    val softBeige = Color(0xFFE8D8C3)
    val textSoft = Color(0xFF8D7B68)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "PawlyApp Logo",
                    modifier = Modifier.size(105.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Welcome",
                    style = MaterialTheme.typography.headlineMedium,
                    color = darkBrown,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "ingresa para poder cuidar a tu mascota",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSoft,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    placeholder = { Text("Ingresa tu nombre de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBrown,
                        unfocusedBorderColor = softBeige,
                        focusedLabelColor = primaryBrown,
                        unfocusedLabelColor = textSoft,
                        cursorColor = primaryBrown,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it
                                    viewModel.clearError()},
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                        } else {
                        PasswordVisualTransformation()

                    },

                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {
                            Icon(imageVector = if (passwordVisible) { Icons.Filled.VisibilityOff }
                            else {
                                Icons.Filled.Visibility
                                },

                                contentDescription =
                                    if (passwordVisible) { "Ocultar contraseña" }
                                else {
                                    "Mostrar contraseña"
                                }
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBrown,
                        unfocusedBorderColor = softBeige,
                        focusedLabelColor = primaryBrown,
                        unfocusedLabelColor = textSoft,
                        cursorColor = primaryBrown,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    enabled = !uiState.isLoading
                )
                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                Button(
                    onClick = { viewModel.login(username,
                        password) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBrown,
                        contentColor = Color.White
                    )
                ) {
                    Text("Iniciar sesión")
                }

                }

                Spacer(modifier = Modifier.height(24.dp))

            }
        }
    }
