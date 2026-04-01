package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pilar.newcomapp.ui.viewmodel.PartidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurarJugadoresScreen(
    onAtras: () -> Unit,
    viewModel: PartidoViewModel = hiltViewModel()
) {
    val rotacion by viewModel.rotacionActual.collectAsState()
    val partido by viewModel.partidoActivo.collectAsState()

    // Configuracion del partido
    var modalidad by remember(partido) { mutableStateOf(partido?.modalidad ?: "Masculino") }
    var categoria by remember(partido) { mutableStateOf(partido?.categoria ?: "+40") }
    var cantidadSets by remember(partido) { mutableStateOf(partido?.cantidadSets ?: 3) }
    var puntajePorSet by remember(partido) { mutableStateOf(partido?.puntajePorSet ?: 15) }

    // Jugadores
    val nombres = remember(rotacion) {
        mutableStateListOf(
            rotacion?.posicion1 ?: "",
            rotacion?.posicion2 ?: "",
            rotacion?.posicion3 ?: "",
            rotacion?.posicion4 ?: "",
            rotacion?.posicion5 ?: "",
            rotacion?.posicion6 ?: ""
        )
    }
    val sexos = remember(rotacion) {
        mutableStateListOf(
            rotacion?.sexo1 ?: "M",
            rotacion?.sexo2 ?: "M",
            rotacion?.sexo3 ?: "M",
            rotacion?.sexo4 ?: "M",
            rotacion?.sexo5 ?: "M",
            rotacion?.sexo6 ?: "M"
        )
    }

    val modalidades = listOf("Masculino", "Femenino", "Mixto")
    val categorias = listOf("Open", "+40", "+45", "+50", "+55", "+60", "+65", "+68")
    val opcionesSets = listOf(1, 3)
    val opcionesPuntaje = listOf(15, 21)

    val etiquetasPosicion = listOf(
        "P1 - Servidor",
        "P2 - Atacante Derecha",
        "P3 - Atacante Centro",
        "P4 - Atacante Izquierda",
        "P5 - Zaguero Izquierda",
        "P6 - Zaguero Central"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Partido y Jugadores") },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
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

            // === SECCION PARTIDO ===
            Text("Configuracion del Partido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)

            // Modalidad
            Text("Modalidad:", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                modalidades.forEach { mod ->
                    FilterChip(
                        selected = modalidad == mod,
                        onClick = { modalidad = mod },
                        label = { Text(mod) }
                    )
                }
            }

            // Categoria
            Text("Categoria:", fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categorias.take(4).forEach { cat ->
                    FilterChip(
                        selected = categoria == cat,
                        onClick = { categoria = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categorias.drop(4).forEach { cat ->
                    FilterChip(
                        selected = categoria == cat,
                        onClick = { categoria = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Sets
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Sets:", fontWeight = FontWeight.SemiBold)
                opcionesSets.forEach { s ->
                    FilterChip(
                        selected = cantidadSets == s,
                        onClick = { cantidadSets = s },
                        label = { Text(if (s == 1) "1 set" else "$s sets") }
                    )
                }
            }

            // Puntaje por set
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Puntos/Set:", fontWeight = FontWeight.SemiBold)
                opcionesPuntaje.forEach { p ->
                    FilterChip(
                        selected = puntajePorSet == p,
                        onClick = { puntajePorSet = p },
                        label = { Text("$p") }
                    )
                }
            }

            HorizontalDivider()

            // === SECCION JUGADORES ===
            Text("Jugadores en Cancha",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)

            Text(
                "Azul = Masculino | Rojo = Femenino",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            (0..5).forEach { i ->
                val colorBorde = when {
                    sexos[i] == "F" -> Color(0xFFC62828)
                    else -> Color(0xFF1565C0)
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            sexos[i] == "F" -> Color(0xFFFFEBEE)
                            else -> Color(0xFFE3F2FD)
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = etiquetasPosicion[i],
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colorBorde
                        )
                        OutlinedTextField(
                            value = nombres[i],
                            onValueChange = { nombres[i] = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        // Sexo (solo en Mixto)
                        if (modalidad == "Mixto") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Sexo:", fontSize = 13.sp)
                                FilterChip(
                                    selected = sexos[i] == "M",
                                    onClick = { sexos[i] = "M" },
                                    label = { Text("Masculino") }
                                )
                                FilterChip(
                                    selected = sexos[i] == "F",
                                    onClick = { sexos[i] = "F" },
                                    label = { Text("Femenino") }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.actualizarConfiguracionPartido(
                        modalidad, categoria, cantidadSets, puntajePorSet
                    )
                    // No pasar liberos - ahora se manejan desde los botones de libero
                    val liberosVacios = List(6) { false }
                    viewModel.guardarNombresJugadores(
                        nombres.toList(),
                        sexos.toList(),
                        liberosVacios
                    )
                    onAtras()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
