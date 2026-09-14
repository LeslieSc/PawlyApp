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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pawlyapp.ui.health.homeHealth.view.HomeHealthView
import com.example.pawlyapp.ui.home.view.PetsHomeView
import com.example.pawlyapp.ui.login.view.LoginScreenView
import com.example.pawlyapp.ui.mainmenu.firstapirequest.view.FirstApiRequestView
import com.example.pawlyapp.ui.mainmenu.homeMainmenu.view.HomeMainMenuView
import com.example.pawlyapp.ui.onboarding.data.OnboardingPreferences
import com.example.pawlyapp.ui.onboarding.view.OnboardingView
import com.example.pawlyapp.ui.personalinformation.homePersonalinformation.view.HomePersonalinformationView
import com.example.pawlyapp.ui.tracker.homeTracker.view.HomeTrackerView

private const val FIRST_API_REQUEST_ROUTE = "first_api_request"

sealed class AppRoute(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object MainMenu :
        AppRoute(
            "main_menu",
            "Explorar",
            Icons.Filled.Pets
        )

    object Home :
        AppRoute(
            "home",
            "Inicio",
            Icons.Filled.Home
        )

    object Health :
        AppRoute(
            "health",
            "Salud",
            Icons.Filled.Favorite
        )

    object Tracker :
        AppRoute(
            "tracker",
            "Tracker",
            Icons.Filled.LocationOn
        )

    object PersonalInfo :
        AppRoute(
            "personal_info",
            "Perfil",
            Icons.Filled.Person
        )
}

private val TABS = listOf(
    AppRoute.Home,
    AppRoute.MainMenu,
    AppRoute.Health,
    AppRoute.Tracker,
    AppRoute.PersonalInfo
)

@Composable
fun AppNavigation() {

    val rootNavController = rememberNavController()
    val context = LocalContext.current

    val startDestination = remember {
        if (
            OnboardingPreferences(context)
                .hasCompletedOnboarding()
        ) {
            "login"
        } else {
            "onboarding"
        }
    }

    NavHost(
        navController = rootNavController,
        startDestination = startDestination
    ) {

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

        composable("login") {

            LoginScreenView(
                onLoginClick = {

                    rootNavController.navigate("tabs") {

                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("tabs") {

            TabsScaffold()
        }
    }
}

@Composable
private fun TabsScaffold() {

    val navController = rememberNavController()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {

            NavigationBar {

                TABS.forEach { tab ->

                    NavigationBarItem(
                        selected =
                            currentRoute == tab.route,

                        onClick = {

                            navController.navigate(
                                tab.route
                            ) {

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

        NavHost(
            navController = navController,
            startDestination = AppRoute.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(
                AppRoute.MainMenu.route
            ) {

                HomeMainMenuView(
                    onNavigateToFirstApiRequest = {

                        navController.navigate(
                            FIRST_API_REQUEST_ROUTE
                        )
                    }
                )
            }

            composable(
                AppRoute.Home.route
            ) {

                PetsHomeView()
            }

            composable(
                FIRST_API_REQUEST_ROUTE
            ) {

                FirstApiRequestView(
                    onBack = {

                        navController.popBackStack()
                    }
                )
            }

            composable(
                AppRoute.Health.route
            ) {

                HomeHealthView()
            }

            composable(
                AppRoute.Tracker.route
            ) {

                HomeTrackerView()
            }

            composable(
                AppRoute.PersonalInfo.route
            ) {

                HomePersonalinformationView()
            }
        }
    }
}