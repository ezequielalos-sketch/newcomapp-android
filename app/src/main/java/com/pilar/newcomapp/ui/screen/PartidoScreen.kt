package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pilar.newcomapp.ui.viewmodel.PartidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartidoScreen(
    onVerRotacion: () -> Unit,
    onAtras: () -> Unit,
    viewModel: PartidoViewModel = hiltViewModel()
) {
    val partido by viewModel.partidoActivo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Partido") },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atras")
                    }
                },
                actions = {
                    IconButton(onClick = onVerRotacion) {
                        Icon(Icons.Default.GridOn, contentDescription = "Rotacion")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            partido?.let { p ->
                // Sets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Sets: ${p.setsLocal} - ${p.setsVisitante}",
                        fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Set ${p.setActual}", fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(24.dp))

                // Marcador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Equipo Local
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(p.nombreEquipoLocal, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${p.puntosLocal}",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            IconButton(onClick = { viewModel.restarPuntoLocal() }) {
                                Icon(Icons.Default.Remove, contentDescription = "-1")
                            }
                            IconButton(onClick = { viewModel.sumarPuntoLocal() }) {
                                Icon(Icons.Default.Add, contentDescription = "+1")
                            }
                        }
                    }

                    Text(":", fontSize = 48.sp, fontWeight = FontWeight.Bold)

                    // Equipo Visitante
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(p.nombreEquipoVisitante, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${p.puntosVisitante}",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            IconButton(onClick = { viewModel.restarPuntoVisitante() }) {
                                Icon(Icons.Default.Remove, contentDescription = "-1")
                            }
                            IconButton(onClick = { viewModel.sumarPuntoVisitante() }) {
                                Icon(Icons.Default.Add, contentDescription = "+1")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.finalizarSet() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar Set")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onVerRotacion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver / Rotar")
                }
            } ?: run {
                Text("No hay partido activo")
            }
        }
    }
}
