package com.pilar.newcomapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pilar.newcomapp.ui.viewmodel.PartidoViewModel

@Composable
fun InicioScreen(
    onNuevoPartido: () -> Unit,
    onVerHistorial: () -> Unit,
    onContinuarPartido: () -> Unit,
    viewModel: PartidoViewModel = hiltViewModel()
) {
    val partidoActivo by viewModel.partidoActivo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "NEWCOM",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Control de Rotaciones",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))

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
        OutlinedButton(
            onClick = onVerHistorial,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Historial")
        }
    }
}
