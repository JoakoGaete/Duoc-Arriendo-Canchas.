package com.example.uinavegacion.ui.screen


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.util.Log
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

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.uinavegacion.data.local.Storage.UserPreferences

import com.example.uinavegacion.ui.viewmodel.BookingUiState
import com.example.uinavegacion.ui.viewmodel.BookingViewModel
import com.example.uinavegacion.ui.viewmodel.CanchasViewModel

import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
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
    canchasVM: CanchasViewModel,
    onSubmitSuccess: () -> Unit
) {
    val state = vm.uiState
    val fields = canchasVM.uiState.fields

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(false)
    val userId by userPrefs.userId.collectAsState(initial = null)

    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Cargar canchas
    LaunchedEffect(Unit) {
        canchasVM.loadFields()
    }

    // Cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUriString = pendingCaptureUri?.toString()
            Toast.makeText(context, "Foto tomada", Toast.LENGTH_SHORT).show()
        } else {
            pendingCaptureUri = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        Text("Reserva Cancha", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        if (!isLoggedIn) {
            Text(
                "Inicia sesión para reservar",
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            return@Column
        }

        // --- Selección cancha ---
        Box {
            OutlinedTextField(
                value = state.fieldId?.let { id -> fields.find { it.id == id }?.name } ?: "",
                readOnly = true,
                onValueChange = {},
                label = { Text("Cancha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (fields.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Cargando canchas...") },
                        onClick = {}
                    )
                } else {
                    fields.forEach { f ->
                        DropdownMenuItem(
                            text = { Text(f.name ?: "Sin nombre") },
                            onClick = {
                                f.id?.let { id -> vm.onFieldSelected(id) }
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- Fecha ---
        val calendar = Calendar.getInstance()
        OutlinedTextField(
            value = state.bookingDate?.toString() ?: "",
            readOnly = true,
            onValueChange = {},
            label = { Text("Fecha de reserva") },
            modifier = Modifier.clickable {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                        vm.onDateSelected(selectedDate)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        )

        Spacer(Modifier.height(16.dp))

        // --- Hora ---
        OutlinedTextField(
            value = state.startTime,
            readOnly = true,
            onValueChange = {},
            label = { Text("Hora") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val c = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, h, m -> vm.onTimeSelected(String.format("%02d:%02d", h, m)) },
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        true
                    ).show()
                }
        )

        Spacer(Modifier.height(20.dp))

        // --- Foto ---
        Button(
            onClick = {
                val file = createTempImageFile(context)
                val uri = getImageUriForFile(context, file)
                pendingCaptureUri = uri
                takePictureLauncher.launch(uri)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E811F))
        ) {
            Text(if (photoUriString == null) "Tomar Foto de carnet" else "Repetir Foto")
        }

        photoUriString?.let { uriString ->
            AsyncImage(
                model = Uri.parse(uriString),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(20.dp))

        // --- Botón Reservar ---
        Button(
            onClick = {
                userId?.let { id ->
                    vm.submitBooking(id) { onSubmitSuccess() }
                }
            },
            enabled = state.canSubmit && photoUriString != null,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E811F))
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
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
            Text(it, color = Color.Red)
        }
    }
}









