package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pilar.newcomapp.ui.viewmodel.PartidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurarJugadoresScreen(
    onAtras: () -> Unit,
    viewModel: PartidoViewModel = hiltViewModel()
) {
    val rotacion by viewModel.rotacionActual.collectAsState()

    var nombre1 by remember(rotacion) { mutableStateOf(rotacion?.posicion1 ?: "") }
    var nombre2 by remember(rotacion) { mutableStateOf(rotacion?.posicion2 ?: "") }
    var nombre3 by remember(rotacion) { mutableStateOf(rotacion?.posicion3 ?: "") }
    var nombre4 by remember(rotacion) { mutableStateOf(rotacion?.posicion4 ?: "") }
    var nombre5 by remember(rotacion) { mutableStateOf(rotacion?.posicion5 ?: "") }
    var nombre6 by remember(rotacion) { mutableStateOf(rotacion?.posicion6 ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Jugadores") },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atras")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ingresa los nombres de los 6 jugadores.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Las posiciones corresponden al diagrama de la cancha:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider()

            Text("-- Red (frente) --", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = nombre4,
                onValueChange = { nombre4 = it },
                label = { Text("Posicion 4 (adelante izquierda)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombre3,
                onValueChange = { nombre3 = it },
                label = { Text("Posicion 3 (adelante centro)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombre2,
                onValueChange = { nombre2 = it },
                label = { Text("Posicion 2 (adelante derecha)") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider()

            Text("-- Fondo --", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = nombre5,
                onValueChange = { nombre5 = it },
                label = { Text("Posicion 5 (atras izquierda)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombre6,
                onValueChange = { nombre6 = it },
                label = { Text("Posicion 6 (atras centro)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombre1,
                onValueChange = { nombre1 = it },
                label = { Text("Posicion 1 (atras derecha - servidor)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.guardarNombresJugadores(
                        listOf(nombre1, nombre2, nombre3, nombre4, nombre5, nombre6)
                    )
                    onAtras()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Jugadores")
            }
        }
    }
}
