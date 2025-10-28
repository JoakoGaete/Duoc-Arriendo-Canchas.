package com.example.uinavegacion.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton

import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil.compose.AsyncImage
import coil.request.ImageRequest

import com.example.uinavegacion.ui.viewmodel.BookingUiState
import com.example.uinavegacion.ui.viewmodel.BookingViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



private fun createTempImageFile(context: Context): File{
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply {
        if(!exists()) mkdirs()
    }
    return File(storageDir,"IMG_${timeStamp}.jpg")
}
private fun getImageUriForFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context,authority,file)
}

@Composable
fun BookingScreen(
    vm: BookingViewModel,
    state: BookingUiState,
    onSubmit: () -> Unit
) {
    val state by vm.booking.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUriString = pendingCaptureUri?.toString()
            Toast.makeText(context, "Foto tomada correctamente", Toast.LENGTH_SHORT).show()
        } else {
            pendingCaptureUri = null
            Toast.makeText(context, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            Toast.makeText(context, "¡Reservado con éxito!", Toast.LENGTH_SHORT).show()
            vm.clearBookingResult()
        }
    }

    // --- Layout principal ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Reserva Cancha",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        // ---------- Selector de Cancha ----------
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                value = state.fieldId?.let { it.toString() } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Cancha") },
                placeholder = { Text("Selecciona cancha") },
                leadingIcon = { Icon(Icons.Default.SportsSoccer, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                vm.fields.collectAsState().value.forEach { field ->
                    DropdownMenuItem(
                        text = { Text(field.name) },
                        onClick = {
                            vm.onFieldSelected(field.id)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---------- Selector de Fecha ----------
        OutlinedTextField(
            value = state.bookingDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha") },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val selectedCalendar = Calendar.getInstance()
                            selectedCalendar.set(year, month, dayOfMonth)
                            if (selectedCalendar.before(Calendar.getInstance())) {
                                Toast.makeText(
                                    context,
                                    "No puedes seleccionar una fecha pasada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                vm.onDateSelected("$dayOfMonth/${month + 1}/$year")
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).apply {
                        datePicker.minDate = calendar.timeInMillis
                    }.show()
                }
        )

        Spacer(Modifier.height(16.dp))

        // ---------- Selector de Hora ----------
        OutlinedTextField(
            value = state.startTime,
            onValueChange = {},
            readOnly = true,
            label = { Text("Hora") },
            leadingIcon = { Icon(Icons.Default.AccessTimeFilled, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            if (hourOfDay in 10..22) {
                                vm.onTimeSelected(String.format("%02d:%02d", hourOfDay, minute))
                            } else {
                                Toast.makeText(
                                    context,
                                    "Horario permitido: 10:00 a 22:00",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }
        )

        Spacer(Modifier.height(24.dp))

        // ---------- Botón Reservar ----------
        Button(
            onClick = onSubmit,
            enabled = state.canSubmit && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text("Reservando...")
            } else {
                Text("Reservar")
            }
        }

        state.errorMsg?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(32.dp))

        // ---------- Sección de Cámara ----------
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Por favror agrega una foto de tu cedula de identidad",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                if (photoUriString.isNullOrEmpty()) {
                    Text(
                        text = "No hay foto",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(Uri.parse(photoUriString))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto Tomada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(12.dp))
                }

                var showDialog by remember { mutableStateOf(false) }

                Button(onClick = {
                    val file = createTempImageFile(context)
                    val uri = getImageUriForFile(context, file)
                    pendingCaptureUri = uri
                    takePictureLauncher.launch(uri)
                }) {
                    Text(
                        if (photoUriString.isNullOrEmpty()) "Abrir Cámara"
                        else "Volver a tomar"
                    )
                }

                if (!photoUriString.isNullOrEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { showDialog = true }) {
                        Text("Eliminar Foto")
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirmación") },
                        text = { Text("¿Desea eliminar la foto?") },
                        confirmButton = {
                            TextButton(onClick = {
                                photoUriString = null
                                showDialog = false
                                Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT)
                                    .show()
                            }) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}
