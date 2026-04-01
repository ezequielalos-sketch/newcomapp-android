package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.pilar.newcomapp.data.local.entity.PartidoEntity
import com.pilar.newcomapp.ui.viewmodel.PartidoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onAtras: () -> Unit,
    viewModel: PartidoViewModel = hiltViewModel()
) {
    val partidos by viewModel.historialPartidos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Partidos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
                    }
                }
            )
        }
    ) { padding ->
        if (partidos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay partidos registrados",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(partidos) { partido ->
                    PartidoHistorialCard(partido)
                }
            }
        }
    }
}

@Composable
fun PartidoHistorialCard(partido: PartidoEntity) {
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(partido.fechaCreacion))

    val estado = if (partido.finalizado) "Finalizado" else "En curso"
    val colorEstado = if (partido.finalizado) Color(0xFF2E7D32) else Color(0xFFE65100)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Fecha y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = fecha, fontSize = 12.sp, color = Color.Gray)
                Surface(
                    color = colorEstado.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = estado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorEstado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Equipos y marcador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = partido.nombreEquipoLocal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1565C0),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${partido.setsLocal} - ${partido.setsVisitante}",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = partido.nombreEquipoVisitante,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFC62828),
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Detalle de sets
            if (partido.set1Local >= 0) {
                Text("Set 1: ${partido.set1Local} - ${partido.set1Visitante}", fontSize = 12.sp, color = Color.Gray)
            }
            if (partido.set2Local >= 0) {
                Text("Set 2: ${partido.set2Local} - ${partido.set2Visitante}", fontSize = 12.sp, color = Color.Gray)
            }
            if (partido.set3Local >= 0) {
                Text("Set 3: ${partido.set3Local} - ${partido.set3Visitante}", fontSize = 12.sp, color = Color.Gray)
            }

            // Info partido
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${partido.modalidad} | ${partido.categoria} | ${partido.cantidadSets} sets a ${partido.puntajePorSet}",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}
