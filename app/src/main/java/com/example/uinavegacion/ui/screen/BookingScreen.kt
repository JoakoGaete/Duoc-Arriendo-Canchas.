package com.example.uinavegacion.ui.screen

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.uinavegacion.ui.viewmodel.BookingUiState
import com.example.uinavegacion.ui.viewmodel.BookingViewModel
import java.util.Calendar
import kotlin.Long
@Composable
fun BookingScreen(
    vm: BookingViewModel,
    state: BookingUiState,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    val state by vm.booking.collectAsStateWithLifecycle()

    LaunchedEffect (state.success) {
        if (state.success) {
            Toast.makeText(context, "¡Reservado con éxito!", Toast.LENGTH_SHORT).show()
            vm.clearBookingResult()  // Limpia el flag para no mostrar repetidamente

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                value = state.fieldId?.let { it.toString() } ?: "Selecciona cancha",
                onValueChange = {},
                readOnly = true,
                label = { Text("Cancha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )
            DropdownMenu (expanded = expanded, onDismissRequest = { expanded = false }) {
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
                                Toast.makeText(context, "No puedes seleccionar una fecha pasada", Toast.LENGTH_SHORT).show()
                            } else {
                                vm.onDateSelected("${dayOfMonth}/${month + 1}/$year")
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).apply {
                        datePicker.minDate = calendar.timeInMillis // evita días pasados
                    }.show()
                }
        )

        Spacer(Modifier.height(16.dp))

        Spacer(Modifier.height(16.dp))

        // ---------- Selector de Hora ----------
        OutlinedTextField(
            value = state.startTime,
            onValueChange = {},
            readOnly = true,
            label = { Text("Hora") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                    val calendar = Calendar.getInstance()

                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            if (hourOfDay in 10..22) { // rango permitido
                                vm.onTimeSelected(String.format("%02d:%02d", hourOfDay, minute))
                            } else {
                                Toast.makeText(context, "Horario permitido: 10:00 a 22:00", Toast.LENGTH_SHORT).show()
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }
        )

        Spacer(Modifier.height(24.dp))

        Button (
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
    }
}
