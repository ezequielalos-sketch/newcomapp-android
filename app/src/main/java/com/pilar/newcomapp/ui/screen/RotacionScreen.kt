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

val ColorMasculino = Color(0xFF1565C0)      // Azul oscuro
val ColorFemenino = Color(0xFFC62828)       // Rojo oscuro
val ColorLibero = Color(0xFF2E7D32)         // Verde oscuro
val ColorFondoMasculino = Color(0xFFE3F2FD) // Azul claro
val ColorFondoFemenino = Color(0xFFFFEBEE)  // Rojo claro
val ColorFondoLibero = Color(0xFFE8F5E9)    // Verde claro

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
            rotacion?.let { rot ->
                val nombres = listOf(rot.posicion1, rot.posicion2, rot.posicion3,
                    rot.posicion4, rot.posicion5, rot.posicion6)
                val sexos = listOf(rot.sexo1, rot.sexo2, rot.sexo3,
                    rot.sexo4, rot.sexo5, rot.sexo6)
                val liberos = listOf(rot.libero1, rot.libero2, rot.libero3,
                    rot.libero4, rot.libero5, rot.libero6)

                // Indicador de red
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFFFF6F00)
                ) {}
                Text(
                    text = "RED",
                    fontSize = 11.sp,
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
                    listOf(3 to 3, 2 to 2, 1 to 1).forEach { (indice, pos) ->
                        JugadorCard(
                            posicion = pos + 1, // posicion 4,3,2 -> indices 3,2,1
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
                    listOf(4 to 4, 5 to 5, 0 to 0).forEach { (indice, pos) ->
                        JugadorCard(
                            posicion = if (pos == 0) 1 else pos + 1,
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
                            modifier = Modifier.padding(8.dp),
                            color = Color(0xFFE65100),
                            fontSize = 12.sp,
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
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF37474F)
                        )
                    ) {
                        Text("<< Rotar -1", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.rotarSiguiente() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5E20)
                        )
                    ) {
                        Text("Rotar +1 >>", fontWeight = FontWeight.Bold)
                    }
                }

            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando rotacion...")
                    }
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
        esLibero -> "LIBERO"
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
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Numero de posicion
            Text(
                text = "$posicion",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorTexto
            )
            // Nombre del jugador
            Text(
                text = if (nombre.isBlank()) "---" else nombre,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Etiqueta sexo / libero
            Surface(
                color = colorBorde,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = etiqueta,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
