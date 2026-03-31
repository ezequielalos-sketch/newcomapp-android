package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pilar.newcomapp.ui.viewmodel.PartidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RotacionScreen(
    onAtras: () -> Unit,
    viewModel: PartidoViewModel = hiltViewModel()
) {
    val rotacion by viewModel.rotacionActual.collectAsState()
    val partido by viewModel.partidoActivo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rotacion - ${partido?.nombreEquipoLocal ?: ""}") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            rotacion?.let { rot ->
                val jugadores = listOf(
                    rot.posicion1, rot.posicion2, rot.posicion3,
                    rot.posicion4, rot.posicion5, rot.posicion6
                )

                Text("-- Red --", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(4 to jugadores[3], 3 to jugadores[2], 2 to jugadores[1]).forEach { (pos, nombre) ->
                        PosicionCard(posicion = pos, nombre = nombre,
                            onNombreChange = { viewModel.actualizarJugadorEnPosicion(pos, it) })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(5 to jugadores[4], 6 to jugadores[5], 1 to jugadores[0]).forEach { (pos, nombre) ->
                        PosicionCard(posicion = pos, nombre = nombre,
                            onNombreChange = { viewModel.actualizarJugadorEnPosicion(pos, it) })
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.rotarAnterior() },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text("<< Rotar -1")
                    }
                    Button(
                        onClick = { viewModel.rotarSiguiente() },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("Rotar +1 >>")
                    }
                }
            } ?: run {
                Text("No hay rotacion disponible")
            }
        }
    }
}

@Composable
fun PosicionCard(
    posicion: Int,
    nombre: String,
    onNombreChange: (String) -> Unit
) {
    var editando by remember { mutableStateOf(false) }
    var texto by remember(nombre) { mutableStateOf(nombre) }

    Card(
        modifier = Modifier
            .size(width = 100.dp, height = 80.dp)
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "P$posicion",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (editando) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                    trailingIcon = {
                        IconButton(onClick = {
                            onNombreChange(texto)
                            editando = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "OK",
                                modifier = Modifier.size(16.dp))
                        }
                    }
                )
            } else {
                Text(
                    text = if (nombre.isBlank()) "Tap" else nombre,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                IconButton(
                    onClick = { editando = true },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar",
                        modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
