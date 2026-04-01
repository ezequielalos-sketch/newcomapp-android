package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
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

val ColorMasculino = Color(0xFF1565C0) // Azul oscuro
val ColorFemenino = Color(0xFFC62828) // Rojo oscuro
val ColorLibero = Color(0xFF2E7D32) // Verde oscuro
val ColorFondoMasculino = Color(0xFFE3F2FD) // Azul claro
val ColorFondoFemenino = Color(0xFFFFEBEE) // Rojo claro
val ColorFondoLibero = Color(0xFFE8F5E9) // Verde claro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RotacionScreen(
    onAtras: () -> Unit,
    onConfigurarJugadores: () -> Unit,
    viewModel: PartidoViewModel = hiltViewModel()
) {
    val rotacion by viewModel.rotacionActual.collectAsState()
    val partido by viewModel.partidoActivo.collectAsState()
    val advertencia by viewModel.advertenciaMixto.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = partido?.nombreEquipoLocal ?: "Rotacion",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (partido != null) {
                            Text(
                                text = "${partido!!.modalidad} | ${partido!!.categoria}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atras")
                    }
                },
                actions = {
                    IconButton(onClick = onConfigurarJugadores) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mini Marcador (aprox 20-25% de la pantalla)
            partido?.let { p ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Local
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(p.nombreEquipoLocal.take(8), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                            Text("${p.puntosLocal}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF1565C0))
                        }
                        
                        // Sets y VS
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SET ${p.setActual}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("${p.setsLocal} - ${p.setsVisitante}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Row {
                                IconButton(onClick = { viewModel.sumarPuntoLocal() }, modifier = Modifier.size(32.dp)) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1565C0)) {
                                        Text("+", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))
                                    }
                                }
                                IconButton(onClick = { viewModel.sumarPuntoVisitante() }, modifier = Modifier.size(32.dp)) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFC62828)) {
                                        Text("+", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))
                                    }
                                }
                            }
                        }

                        // Visitante
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(p.nombreEquipoVisitante.take(8), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                            Text("${p.puntosVisitante}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFFC62828))
                        }
                    }
                }
            }

            rotacion?.let { rot ->
                val nombres = listOf(rot.posicion1, rot.posicion2, rot.posicion3, rot.posicion4, rot.posicion5, rot.posicion6)
                val sexos = listOf(rot.sexo1, rot.sexo2, rot.sexo3, rot.sexo4, rot.sexo5, rot.sexo6)
                val liberos = listOf(rot.libero1, rot.libero2, rot.libero3, rot.libero4, rot.libero5, rot.libero6)

                // Indicador de red
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = Color(0xFFFF6F00)
                ) {}
                Text(
                    text = "RED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6F00),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                // Fila delantera (posiciones 4, 3, 2)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(3, 2, 1).forEach { indice ->
                        JugadorCard(
                            posicion = indice + 1,
                            nombre = nombres[indice],
                            sexo = sexos[indice],
                            esLibero = liberos[indice],
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Fila trasera (posiciones 5, 6, 1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(4, 5, 0).forEach { indice ->
                        JugadorCard(
                            posicion = if (indice == 0) 1 else indice + 1,
                            nombre = nombres[indice],
                            sexo = sexos[indice],
                            esLibero = liberos[indice],
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }

                // Advertencia mixto
                if (advertencia.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = advertencia,
                            modifier = Modifier.padding(4.dp),
                            color = Color(0xFFE65100),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Botones de rotacion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.rotarAnterior() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
                    ) {
                        Text("<< ROTAR -1", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.rotarSiguiente() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                    ) {
                        Text("ROTAR +1 >>", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun JugadorCard(
    posicion: Int,
    nombre: String,
    sexo: String,
    esLibero: Boolean,
    modifier: Modifier = Modifier
) {
    val colorFondo = when {
        esLibero -> ColorFondoLibero
        sexo == "F" -> ColorFondoFemenino
        else -> ColorFondoMasculino
    }
    val colorBorde = when {
        esLibero -> ColorLibero
        sexo == "F" -> ColorFemenino
        else -> ColorMasculino
    }
    val colorTexto = when {
        esLibero -> ColorLibero
        sexo == "F" -> ColorFemenino
        else -> ColorMasculino
    }
    val etiqueta = when {
        esLibero -> "LIB"
        sexo == "F" -> "F"
        else -> "M"
    }

    Card(
        modifier = modifier
            .border(2.dp, colorBorde, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$posicion",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorTexto
            )
            Text(
                text = if (nombre.isBlank()) "---" else nombre,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                color = colorBorde,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = etiqueta,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}
