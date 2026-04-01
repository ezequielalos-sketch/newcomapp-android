package com.pilar.newcomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pilar.newcomapp.ui.screen.ConfigurarJugadoresScreen
import com.pilar.newcomapp.ui.screen.HistorialScreen
import com.pilar.newcomapp.ui.screen.InicioScreen
import com.pilar.newcomapp.ui.screen.PartidoScreen
import com.pilar.newcomapp.ui.screen.RotacionScreen
import com.pilar.newcomapp.ui.theme.NewcomappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewcomappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "inicio"
                    ) {
                        composable("inicio") {
                            InicioScreen(
                                onNuevoPartido = {
                                    navController.navigate("partido") {
                                        popUpTo("inicio") { inclusive = false }
                                    }
                                },
                                onVerHistorial = {
                                    navController.navigate("historial")
                                },
                                onContinuarPartido = {
                                    navController.navigate("partido") {
                                        popUpTo("inicio") { inclusive = false }
                                    }
                                },
                                onVerRotacion = {
                                    navController.navigate("rotacion")
                                }
                            )
                        }
                        composable("partido") {
                            PartidoScreen(
                                onVerRotacion = { navController.navigate("rotacion") },
                                onAtras = { navController.navigateUp() }
                            )
                        }
                        composable("rotacion") {
                            RotacionScreen(
                                onAtras = { navController.navigateUp() },
                                onConfigurarJugadores = { navController.navigate("configurar") }
                            )
                        }
                        composable("configurar") {
                            ConfigurarJugadoresScreen(
                                onAtras = { navController.navigateUp() }
                            )
                        }
                        composable("historial") {
                            HistorialScreen(
                                onAtras = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}
