package com.example.uinavegacion.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.data.local.Storage.UserPreferences
import com.example.uinavegacion.ui.components.FieldCard
import com.example.uinavegacion.ui.viewmodel.CanchasViewModel


@Composable
fun HomeScreen(
    onGoBooking: () -> Unit,
    onFieldClick: () -> Unit ={},
    canchasviewModel: CanchasViewModel,
    onGoMapa: () -> Unit
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val uiState = canchasviewModel.uiState
    val fields = uiState.fields

    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(false)
    val bg = MaterialTheme.colorScheme.surfaceBright


    LaunchedEffect (Unit) {
        canchasviewModel.loadFields()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                // Cabecera
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Inicio",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF20A239)
                    )
                    Spacer(Modifier.width(8.dp))

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                "Arrienda las mejores canchas de Fútbol aquí, presiona una cancha y procede a arrendar o navega por la app como gustes!!!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },

                        )
                }

                Spacer(Modifier.height(20.dp))
            }

            if (uiState.error != null) {
                item {
                    Text(
                        text = uiState.error ?: "Ups ha pasado un error",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }



            if (uiState.isLoading) {
                item {
                    CircularProgressIndicator()
                }
            }

            // Lista de canchas
            items(fields) { field ->
                FieldCard(
                    field = field,
                    onClick = { onGoBooking() }
                )
            }

            item {
                Spacer(Modifier.height(24.dp))

                // Botones al final
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    Button(onClick = onGoMapa, colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xED023C46),   // color de fondo
                        contentColor = Color.LightGray)) {
                        Text("Ver nuestra ubicación")
                    }
                }

            }
        }
    }
}
