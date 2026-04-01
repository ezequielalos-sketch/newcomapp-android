package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.pilar.newcomapp.data.local.entity.PartidoEntity
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = onVerRotacion) {
                        Icon(Icons.Default.List, contentDescription = "Rotación")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            partido?.let { p ->
                // Nombre de equipos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = p.nombreEquipoLocal, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
                    Text(text = "vs", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                    Text(text = p.nombreEquipoVisitante, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                Text(text = "Set Actual: ${p.setActual}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)

                Spacer(modifier = Modifier.height(32.dp))

                // Marcador de Puntos
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

                Spacer(modifier = Modifier.height(40.dp))

                // Historial de Sets
                Text(
                    text = "HISTORIAL DE SETS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                
                SetResultRow("Set 1", p.set1Local, p.set1Visitante)
                SetResultRow("Set 2", p.set2Local, p.set2Visitante)
                if (p.cantidadSets == 3) {
                    SetResultRow("Set 3", p.set3Local, p.set3Visitante)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onVerRotacion,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("VER ROTACIÓN / JUGADORES", fontWeight = FontWeight.Bold)
                }
                
                if (p.finalizado) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "PARTIDO FINALIZADO",
                        color = Color.Red,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay partido activo")
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
