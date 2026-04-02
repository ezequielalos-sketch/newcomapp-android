package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pilar.newcomapp.ui.viewmodel.PartidoViewModel

val ColorMasculino = Color(0xFF1565C0)
val ColorFemenino = Color(0xFFC62828)
val ColorLiberoM = Color(0xFF1565C0)       // Azul para borde libero M
val ColorLiberoF = Color(0xFFC62828)       // Rojo para borde libero F
val ColorLiberoFondo = Color(0xFFE8F5E9)   // Verde claro fondo ambos liberos
val ColorLiberoTexto = Color(0xFF2E7D32)   // Verde oscuro texto LIB
val ColorFondoMasculino = Color(0xFFE3F2FD)
val ColorFondoFemenino = Color(0xFFFFEBEE)

val NOMBRES_POSICIONES = listOf(
    "P1: Servidor",
    "P2: Atacante Der.",
    "P3: Atacante Cen.",
    "P4: Atacante Izq.",
    "P5: Zaguero Izq.",
    "P6: Zaguero Cen."
)

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
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
            // Mini Marcador
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(p.nombreEquipoLocal.take(8), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                            Text("${p.puntosLocal}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF1565C0))
                        }
                        
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
                val esLiberoEnCancha = listOf(rot.libero1, rot.libero2, rot.libero3, rot.libero4, rot.libero5, rot.libero6)

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

                // Fila delantera (posiciones 4, 3, 2 - Atacantes)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(3, 2, 1).forEach { indice ->
                        JugadorCard(
                            posicion = indice + 1,
                            nombrePosicion = NOMBRES_POSICIONES[indice],
                            nombre = nombres[indice],
                            sexo = sexos[indice],
                            esLibero = esLiberoEnCancha[indice],
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Fila trasera (posiciones 5, 6, 1 - Defensas/Servidor)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(4, 5, 0).forEach { indice ->
                        JugadorCard(
                            posicion = if (indice == 0) 1 else indice + 1,
                            nombrePosicion = NOMBRES_POSICIONES[if (indice == 0) 0 else indice],
                            nombre = nombres[indice],
                            sexo = sexos[indice],
                            esLibero = esLiberoEnCancha[indice],
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

                Spacer(modifier = Modifier.height(6.dp))

                // Botones de Liberos y Nombres
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Boton Nombres
                    OutlinedButton(
                        onClick = onConfigurarJugadores,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Nombres", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    // Boton Libero M (azul con texto verde)
                    Button(
                        onClick = { viewModel.ingresarLibero("M") },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1565C0)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Libero M", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF81C784))
                    }
                    // Boton Libero F (rojo con texto verde)
                    Button(
                        onClick = { viewModel.ingresarLibero("F") },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC62828)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Libero F", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF81C784))
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
    nombrePosicion: String,
    nombre: String,
    sexo: String,
    esLibero: Boolean,
    modifier: Modifier = Modifier
) {
    // Colores segun tipo: Libero M (verde/azul), Libero F (verde/rojo), regular
    val colorFondo = when {
        esLibero -> ColorLiberoFondo
        sexo == "F" -> ColorFondoFemenino
        else -> ColorFondoMasculino
    }
    val colorBorde = when {
        esLibero && sexo == "F" -> ColorLiberoF   // Libero F: borde rojo
        esLibero -> ColorLiberoM                    // Libero M: borde azul
        sexo == "F" -> ColorFemenino
        else -> ColorMasculino
    }
    val colorNumero = when {
        esLibero -> ColorLiberoTexto  // Verde para el numero del libero
        sexo == "F" -> ColorFemenino
        else -> ColorMasculino
    }
    val etiqueta = when {
        esLibero && sexo == "F" -> "LIB F"
        esLibero -> "LIB M"
        sexo == "F" -> "F"
        else -> "M"
    }
    val colorEtiqueta = when {
        esLibero && sexo == "F" -> ColorLiberoF  // Rojo para etiqueta LIB F
        esLibero -> ColorLiberoM                  // Azul para etiqueta LIB M
        sexo == "F" -> ColorFemenino
        else -> ColorMasculino
    }

    val nombreCorto = nombrePosicion.substringAfter(": ", "")

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
            // Nombre de la posicion
            Text(
                text = nombreCorto,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            // Numero de posicion - reducido
            Text(
                text = "$posicion",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorNumero
            )
            // Nombre del jugador - GRANDE 18sp
            Text(
                text = if (nombre.isBlank()) "---" else nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Etiqueta de sexo/libero
            Surface(
                color = colorEtiqueta,
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
