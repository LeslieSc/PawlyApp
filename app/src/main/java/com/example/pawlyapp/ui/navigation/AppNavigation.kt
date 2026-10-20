
package com.example.pawlyapp.ui.navigation

import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.pawlyapp.ui.auth.data.SessionPreferences
import com.example.pawlyapp.ui.auth.view.LoginScreenView
import com.example.pawlyapp.ui.auth.viewmodel.LogoutViewModel

import com.example.pawlyapp.ui.health.homeHealth.view.HomeHealthView
import com.example.pawlyapp.ui.home.view.PetsHomeView


import com.example.pawlyapp.ui.breeds.homeBreeds.view.HomeBreedsView
import com.example.pawlyapp.ui.breeds.view.BreedsView
import com.example.pawlyapp.ui.onboarding.data.OnboardingPreferences
import com.example.pawlyapp.ui.onboarding.view.OnboardingView

import com.example.pawlyapp.ui.personalinformation.homePersonalinformation.view.HomePersonalinformationView

import com.example.pawlyapp.ui.tracker.homeTracker.view.HomeTrackerView

// ========================================
// RUTA DE API
// ========================================

private const val FIRST_API_REQUEST_ROUTE =
    "first_api_request"

// ========================================
// RUTAS PRINCIPALES
// ========================================
private const val BREEDS_ROUTE = "breeds"

sealed class AppRoute(
    val route: String,
    val label: String,
    val icon: ImageVector
) {

    object MainMenu : AppRoute(
        route = "main_menu",
        label = "Explorar",
        icon = Icons.Filled.Pets
    )

    object Home : AppRoute(
        route = "home",
        label = "Inicio",
        icon = Icons.Filled.Home
    )

    object Health : AppRoute(
        route = "health",
        label = "Salud",
        icon = Icons.Filled.Favorite
    )

    object Tracker : AppRoute(
        route = "tracker",
        label = "Tracker",
        icon = Icons.Filled.LocationOn
    )

    object PersonalInfo : AppRoute(
        route = "personal_info",
        label = "Perfil",
        icon = Icons.Filled.Person
    )
}

// ========================================
// PESTAÑAS INFERIORES
// ========================================

private val TABS = listOf(
    AppRoute.Home,
    AppRoute.MainMenu,
    AppRoute.Health,
    AppRoute.Tracker,
    AppRoute.PersonalInfo
)

// ========================================
// NAVEGACION PRINCIPAL
// ========================================

@Composable
fun AppNavigation() {

    val rootNavController = rememberNavController()

    val context = LocalContext.current

    // Decidir si mostrar onboarding, login o inicio
    val startDestination = remember {

        if (
            !OnboardingPreferences(context)
                .hasCompletedOnboarding()
        ) {

            "onboarding"

        } else if (
            SessionPreferences(context).isLoggedIn()
        ) {

            "tabs"

        } else {

            "login"
        }
    }

    NavHost(
        navController = rootNavController,
        startDestination = startDestination
    ) {

        // ====================================
        // ONBOARDING
        // ====================================

        composable("onboarding") {

            OnboardingView(
                onFinishOnboarding = {

                    OnboardingPreferences(context)
                        .setOnboardingCompleted()

                    rootNavController.navigate("login") {

                        popUpTo("onboarding") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // ====================================
        // LOGIN
        // ====================================

        composable("login") {

            LoginScreenView(
                onLoginSuccess = {

                    rootNavController.navigate("tabs") {

                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // ====================================
        // PANTALLAS CON BARRA INFERIOR
        // ====================================

        composable("tabs") {

            val logoutViewModel: LogoutViewModel =
                viewModel()

            TabsScaffold(
                onLogout = {

                    logoutViewModel.logout {

                        rootNavController.navigate("login") {

                            popUpTo("tabs") {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
    }
}

// ========================================
// BARRA INFERIOR Y NAVEGACION DE TABS
// ========================================

@Composable
private fun TabsScaffold(
    onLogout: () -> Unit
) {

    val navController = rememberNavController()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route

    Scaffold(

        // ====================================
        // BARRA DE NAVEGACION
        // ====================================

        bottomBar = {

            NavigationBar {

                TABS.forEach { tab ->

                    NavigationBarItem(

                        selected =
                            currentRoute == tab.route,

                        onClick = {

                            navController.navigate(tab.route) {

                                popUpTo(
                                    navController.graph.startDestinationId
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true

                                restoreState = true
                            }
                        },

                        icon = {

                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },

                        label = {

                            Text(
                                text = tab.label,
                                fontSize = 10.sp
                            )
                        }
                    )
                }
            }
        }

    ) { innerPadding ->

        // ====================================
        // RUTAS DE LAS PANTALLAS
        // ====================================

        NavHost(
            navController = navController,
            startDestination = AppRoute.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // ====================================
            // MENU EXPLORAR
            // ====================================

            composable(
                AppRoute.MainMenu.route
            ) {

                HomeBreedsView()
            }

            // ====================================
            // HOME - F11 EMPTY STATE
            // ====================================

            composable(
                AppRoute.Home.route
            ) {

                PetsHomeView(

                    // Al presionar "Iniciar paseo"
                    // ir directamente al Tracker F04.
                    onStartWalk = {

                        navController.navigate(
                            AppRoute.Tracker.route
                        ) {

                            popUpTo(
                                navController.graph.startDestinationId
                            ) {
                                saveState = true
                            }

                            launchSingleTop = true

                            restoreState = true
                        }
                    }
                )
            }

            // ====================================
            // PRIMERA API
            // ====================================

            composable(
                BREEDS_ROUTE
            ) {

                BreedsView(
                    onBack = {

                        navController.popBackStack()
                    }
                )
            }

            // ====================================
            // SALUD
            // ====================================

            composable(
                AppRoute.Health.route
            ) {

                HomeHealthView()
            }

            // ====================================
            // TRACKER - F04
            // ====================================

            composable(
                AppRoute.Tracker.route
            ) {

                HomeTrackerView()
            }

            // ====================================
            // PERFIL
            // ====================================

            composable(
                AppRoute.PersonalInfo.route
            ) {

                HomePersonalinformationView(
                    onLogout = onLogout
                )
            }
        }
    }
}
