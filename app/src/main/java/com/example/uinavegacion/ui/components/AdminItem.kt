package com.example.uinavegacion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.data.remote.dto.BookingDto

@Composable
fun BookingItemAdmin(reserva: BookingDto, onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text("Usuario ID: ${reserva.userId ?: "N/A"}")
        Text("Cancha: ${reserva.fieldId ?: "N/A"} ")
        Text("Fecha: ${reserva.date ?: "N/A"} ")
        Text("Hora: ${reserva.startTime?: "N/A"} ")
        Text("Estado: ${reserva.status?: "N/A"} ")

        Spacer(modifier = Modifier.height(8.dp))
        Button (
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White),
            modifier = Modifier.align(Alignment.End)
        ) { Text("Eliminar") }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Eliminar reserva") },
                text = { Text("¿Deseas eliminar esta reserva?") },
                confirmButton = {
                    TextButton (onClick = {
                        onDelete()
                        showDialog = false
                    }) { Text("Sí") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("No") }
                }
            )
        }
    }
}