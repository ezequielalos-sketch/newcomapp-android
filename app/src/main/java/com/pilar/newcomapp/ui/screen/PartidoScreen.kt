package com.pilar.newcomapp.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
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
    val finDeSet by viewModel.finDeSetTransicion.collectAsState()

    // Estado local para editar nombres
    var editandoNombres by remember { mutableStateOf(false) }
    var nombreLocal by remember { mutableStateOf("") }
    var nombreVisitante by remember { mutableStateOf("") }
    var nombresIniciados by remember { mutableStateOf(false) }

    LaunchedEffect(partido?.id) {
        partido?.let {
            nombreLocal = it.nombreEquipoLocal
            nombreVisitante = it.nombreEquipoVisitante
            nombresIniciados = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marcador Newcom", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
                    }
                },
                actions = {
                    IconButton(onClick = onVerRotacion) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Rotacion")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                partido?.let { p ->
                    // Nombre de equipos - editable
                    if (editandoNombres) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = nombreLocal,
                                onValueChange = { nombreLocal = it },
                                label = { Text("Local") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = nombreVisitante,
                                onValueChange = { nombreVisitante = it },
                                label = { Text("Visitante") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.actualizarNombresEquipos(nombreLocal, nombreVisitante)
                                editandoNombres = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar nombres")
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { editandoNombres = true },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (nombresIniciados) nombreLocal else p.nombreEquipoLocal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(text = "vs", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                            Text(
                                text = if (nombresIniciados) nombreVisitante else p.nombreEquipoVisitante,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar nombres",
                                tint = Color.Gray,
                                modifier = Modifier.padding(start = 8.dp).size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Marcador de Sets
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SetScoreItem(score = p.setsLocal, color = Color(0xFF1565C0))
                        Text(text = "SETS", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold, color = Color.Gray)
                        SetScoreItem(score = p.setsVisitante, color = Color(0xFFC62828))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!p.finalizado) {
                        Text(text = "Set Actual: ${p.setActual}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Marcador de Puntos (solo si no finalizo)
                    if (!p.finalizado) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScoreColumn(
                                points = p.puntosLocal,
                                onPlus = { viewModel.sumarPuntoLocal() },
                                onMinus = { viewModel.restarPuntoLocal() },
                                color = Color(0xFF1565C0)
                            )

                            ScoreColumn(
                                points = p.puntosVisitante,
                                onPlus = { viewModel.sumarPuntoVisitante() },
                                onMinus = { viewModel.restarPuntoVisitante() },
                                color = Color(0xFFC62828)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Historial de Sets - SIEMPRE visible
                    Text(
                        text = "HISTORIAL DE SETS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    
                    SetResultRow("Set 1", p.set1Local, p.set1Visitante)
                    SetResultRow("Set 2", p.set2Local, p.set2Visitante)
                    if (p.cantidadSets >= 3) {
                        SetResultRow("Set 3", p.set3Local, p.set3Visitante)
                    }

                    // Resultado final si partido termino
                    if (p.finalizado) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("PARTIDO FINALIZADO", color = Color(0xFF2E7D32), fontWeight = FontWeight.Black, fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                val ganador = if (p.setsLocal > p.setsVisitante) {
                                    if (nombresIniciados) nombreLocal else p.nombreEquipoLocal
                                } else {
                                    if (nombresIniciados) nombreVisitante else p.nombreEquipoVisitante
                                }
                                val colorGanador = if (p.setsLocal > p.setsVisitante) Color(0xFF1565C0) else Color(0xFFC62828)
                                Text("Ganador: $ganador", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colorGanador)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Sets: ${p.setsLocal} - ${p.setsVisitante}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onVerRotacion,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("VER ROTACION / JUGADORES", fontWeight = FontWeight.Bold)
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay partido activo")
                    }
                }
            }

            // === OVERLAY: Transicion de fin de set ===
            finDeSet?.let { info ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Fondo semi-transparente
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {}

                    // Card de transicion
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clickable { viewModel.descartarTransicionSet() },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (info.partidoFinalizado) Color(0xFFF1F8E9) else Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (info.partidoFinalizado) {
                                Text(
                                    text = "PARTIDO FINALIZADO",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            } else {
                                Text(
                                    text = "FIN DEL SET ${info.setNumero}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    color = Color(0xFF37474F)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Puntaje final del set
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${info.puntosLocal}",
                                        fontSize = 56.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1565C0)
                                    )
                                }
                                Text(
                                    text = "-",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${info.puntosVisitante}",
                                        fontSize = 56.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFC62828)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val textoGanador = if (info.ganadorLocal) "Gana equipo local" else "Gana equipo visitante"
                            val colorGanador = if (info.ganadorLocal) Color(0xFF1565C0) else Color(0xFFC62828)
                            Text(
                                text = textoGanador,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = colorGanador
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Toca para continuar",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SetScoreItem(score: Int, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = "$score", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ScoreColumn(points: Int, onPlus: () -> Unit, onMinus: () -> Unit, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onPlus, modifier = Modifier.size(80.dp)) {
            Surface(shape = RoundedCornerShape(16.dp), color = color.copy(alpha = 0.1f)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("+", fontSize = 48.sp, color = color, fontWeight = FontWeight.Light)
                }
            }
        }
        
        Text(
            text = "$points",
            fontSize = 80.sp,
            fontWeight = FontWeight.Black,
            color = color,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        OutlinedButton(
            onClick = onMinus,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.width(60.dp)
        ) {
            Text("-1", color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SetResultRow(label: String, local: Int, visitante: Int) {
    if (local == -1) return
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, color = Color.Gray)
        Text(
            text = "$local - $visitante",
            fontWeight = FontWeight.ExtraBold,
            color = if (local > visitante) Color(0xFF1565C0) else Color(0xFFC62828)
        )
    }
}
