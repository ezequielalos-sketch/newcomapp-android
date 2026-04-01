package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                title = { Text("Marcador Newcom", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atras")
                    }
                },
                actions = {
                    IconButton(onClick = onVerRotacion) {
                        Icon(Icons.Default.List, contentDescription = "Rotacion")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            partido?.let { p ->
                // Header con info del partido
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${p.modalidad} - ${p.categoria}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sets: ", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Text(
                                text = "${p.setsLocal}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1565C0)
                            )
                            Text(" - ", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            Text(
                                text = "${p.setsVisitante}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFC62828)
                            )
                        }
                        if (p.finalizado) {
                            Text(
                                text = "PARTIDO FINALIZADO",
                                color = Color.Red,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Marcador Principal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Equipo Local
                    MarcadorEquipo(
                        nombre = p.nombreEquipoLocal,
                        puntos = p.puntosLocal,
                        color = Color(0xFF1565C0), // Azul
                        onSumar = { viewModel.sumarPuntoLocal() },
                        onRestar = { viewModel.restarPuntoLocal() },
                        modifier = Modifier.weight(1f)
                    )

                    // Equipo Visitante
                    MarcadorEquipo(
                        nombre = p.nombreEquipoVisitante,
                        puntos = p.puntosVisitante,
                        color = Color(0xFFC62828), // Rojo
                        onSumar = { viewModel.sumarPuntoVisitante() },
                        onRestar = { viewModel.restarPuntoVisitante() },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Historial de sets
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SetResultItem("Set 1", p.set1Local, p.set1Visitante)
                        SetResultItem("Set 2", p.set2Local, p.set2Visitante)
                        SetResultItem("Set 3", p.set3Local, p.set3Visitante)
                    }
                }

                // Botones de accion
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onVerRotacion,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64))
                    ) {
                        Text("VER ROTACION", fontWeight = FontWeight.Bold)
                    }
                    if (!p.finalizado) {
                        Button(
                            onClick = { viewModel.finalizarSet() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("FORZAR FIN SET", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando partido...")
                }
            }
        }
    }
}

@Composable
fun MarcadorEquipo(
    nombre: String,
    puntos: Int,
    color: Color,
    onSumar: () -> Unit,
    onRestar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(2.dp, color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = nombre.uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = color,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            Text(
                text = "$puntos",
                fontSize = 110.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalButton(
                    onClick = onRestar,
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = color.copy(alpha = 0.2f))
                ) {
                    Text("-", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = color)
                }
                
                Button(
                    onClick = onSumar,
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = color)
                ) {
                    Text("+", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SetResultItem(label: String, local: Int, visitante: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        if (local != -1) {
            Text(
                text = "$local - $visitante",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (local > visitante) Color(0xFF1565C0) else Color(0xFFC62828)
            )
        } else {
            Text("-", fontSize = 16.sp, color = Color.Gray)
        }
    }
}
