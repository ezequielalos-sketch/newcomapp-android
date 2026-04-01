package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pilar.newcomapp.ui.viewmodel.PartidoViewModel

@Composable
fun InicioScreen(
    onNuevoPartido: () -> Unit,
    onVerHistorial: () -> Unit,
    onContinuarPartido: () -> Unit,
    onVerRotacion: () -> Unit,
    viewModel: PartidoViewModel = hiltViewModel()
) {
    val partidoActivo by viewModel.partidoActivo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Titulo
        Text(
            text = "NEWCOM",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Mas zapatillas y menos pastillas",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Botones
        if (partidoActivo != null) {
            Button(
                onClick = onContinuarPartido,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar Partido")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                viewModel.crearNuevoPartido()
                onNuevoPartido()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nuevo Partido")
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (partidoActivo != null) {
            OutlinedButton(
                onClick = onVerRotacion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver / Rotaciones")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onVerHistorial,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Historial")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Cita al pie
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        Text(
            text = "\u201CLos atacantes ganan partidos, las defensas ganan los campeonatos\u201D",
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "\u2014 John Gregory",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
