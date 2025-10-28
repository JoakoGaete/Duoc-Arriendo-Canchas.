package com.example.uinavegacion.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.uinavegacion.data.local.Storage.UserPreferences
import com.example.uinavegacion.ui.components.FieldCard
import com.example.uinavegacion.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun HomeScreen(
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoBooking: () -> Unit,
    onFieldClick: () -> Unit ={},
    viewModel: AuthViewModel,
    onGoMapa: () -> Unit
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val fields by viewModel.fields.collectAsState()
    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(false)
    val bg = MaterialTheme.colorScheme.surfaceBright
    val scope = rememberCoroutineScope()

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
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))

                    AssistChip(
                        onClick = {},
                        label = { Text("Arrienda las mejores canchas de Fútbol aquí, presiona una cancha y procede a arrendar o navega por la app como gustes!!!",
                            style = MaterialTheme.typography.bodyMedium)},

                    )
                }

                Spacer(Modifier.height(20.dp))
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

                    // Botón de Login / Logout
                    Button(
                        onClick = {
                            if (isLoggedIn) {
                                scope.launch {
                                    userPrefs.logout()
                                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                onGoLogin()
                            }
                        }
                    ) {
                        Text(if (isLoggedIn) "Cerrar Sesión" else "Iniciar Sesión")
                    }

                    // Botón de registro
                    OutlinedButton(onClick = onGoRegister) {
                        Text("Ir a Registro")
                    }
                }
                Button(onClick = onGoMapa) {
                    Text("Ver nuestra ubicación")
                }
            }

        }
    }
}
