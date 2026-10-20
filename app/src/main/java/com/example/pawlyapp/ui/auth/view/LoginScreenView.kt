package com.example.pawlyapp.ui.auth.view

import android.content.ContextWrapper
import com.example.pawlyapp.ui.auth.data.SessionPreferences
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.BorderStroke
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
import com.example.pawlyapp.ui.components.PawLoaderOverlay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.width


@Composable
fun LoginScreenView(onLoginSuccess: () -> Unit,
                    viewModel: LoginViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val isBiometricAvailable = remember(context) {
        val manager = BiometricManager.from(context)
        val result = manager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        result == BiometricManager.BIOMETRIC_SUCCESS
    }

    val isBiometricEnabledInApp = remember(uiState) {
        SessionPreferences(context).isBiometricEnabled()
    }

    val showBiometricButton = isBiometricAvailable && isBiometricEnabledInApp

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

    Box(modifier = Modifier.fillMaxSize()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .pointerInput(Unit){
                detectTapGestures (onTap = {
                    focusManager.clearFocus() // cuando tapeamos afuer del teclado se limpia nuestro enfoque osea se baja el teclado

                })
            }
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
                    text = "Bienvenido",
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
                    onValueChange = { username = it.replace(" ", "") },
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
                    onValueChange = { password = it.replace(" ", "")
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
                    Text("Iniciar sesión", fontWeight = FontWeight.Bold)
                }

                if (showBiometricButton) {
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            val activity = context.findFragmentActivity()
                            if (activity != null) {
                                authenticateWithBiometrics(
                                    activity = activity,
                                    onSuccess = {
                                        viewModel.loginWithBiometric()
                                    },
                                    onError = { errorMessage ->
                                        viewModel.setCustomError(errorMessage)
                                    }
                                )
                            } else {
                                viewModel.setCustomError("No se pudo obtener la actividad para la biometría")
                            }
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.5.dp, primaryBrown),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = primaryBrown,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Huella",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Entrar con huella", fontWeight = FontWeight.SemiBold)
                    }
                }

            }
        }
    }

        if (uiState.isLoading) {
            PawLoaderOverlay()
        }

        if (uiState.showBiometricPromptDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.disableBiometricAndProceed() },
                title = { Text("¿Activar acceso biométrico?", color = darkBrown, fontWeight = FontWeight.Bold) },
                text = { Text("¿Deseas usar tu huella dactilar o reconocimiento facial para iniciar sesión en tus próximos accesos?", color = textSoft) },
                confirmButton = {
                    TextButton(onClick = { viewModel.enableBiometricAndProceed() }) {
                        Text("Sí, activar", color = primaryBrown, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.disableBiometricAndProceed() }) {
                        Text("No, gracias", color = textSoft)
                    }
                }
            )
        }
    }
}

private fun authenticateWithBiometrics(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val biometricManager = BiometricManager.from(activity)
    when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {}
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            onError("Este dispositivo no cuenta con sensor biométrico.")
            return
        }
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            onError("El sensor biométrico no está disponible.")
            return
        }
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            onError("No hay huellas o rostros configurados en el dispositivo.")
            return
        }
    }

    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Iniciar sesión con Biometría")
        .setSubtitle("Usa tu huella dactilar para entrar")
        .setNegativeButtonText("Cancelar")
        .build()

    biometricPrompt.authenticate(promptInfo)
}

private fun android.content.Context.findFragmentActivity(): FragmentActivity? {
    var current: android.content.Context? = this
    while (current != null) {
        if (current is FragmentActivity) return current
        current = if (current is ContextWrapper) current.baseContext else null
    }
    return null
}
