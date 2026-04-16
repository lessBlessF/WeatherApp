package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.screens.DetailsScreen
import com.example.weatherapp.ui.screens.MainScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.viewmodel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val app = application as WeatherApp

                    val weatherViewModel: com.example.weatherapp.ui.viewmodel.WeatherViewModel = viewModel(
                        factory = WeatherViewModelFactory(app.repository)
                    )

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "main_screen") {

                        composable("main_screen") {
                            MainScreen(
                                viewModel = weatherViewModel,
                                onNavigateToDetails = { cityName ->
                                    navController.navigate("details_screen/$cityName")
                                }
                            )
                        }

                        composable("details_screen/{cityName}") { backStackEntry ->
                            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
                            DetailsScreen(
                                cityName = cityName,
                                viewModel = weatherViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}