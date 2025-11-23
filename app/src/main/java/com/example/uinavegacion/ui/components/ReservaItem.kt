package com.example.uinavegacion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.sp
import com.example.uinavegacion.data.local.booking.BookingEntity

@Composable
fun ReservaItem(reserva: BookingEntity, onDelete: (Long) -> Unit) {var showDialog by remember { mutableStateOf(false) }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column (
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Cancha: ${reserva.fieldId}", fontSize = 18.sp)
            Text(text = "Fecha: ${reserva.bookingDate}", fontSize = 16.sp, color = Color.DarkGray)
            Text(text = "Horario: ${reserva.startTime}", fontSize = 16.sp, color = Color.DarkGray)
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button (
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            ),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Eliminar")
        }

        // Dialogo de confirmación
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Eliminar reserva") },
                text = { Text("¿Deseas eliminar esta reserva?") },
                confirmButton = {
                    TextButton (onClick = {
                        onDelete(reserva.id)
                        showDialog = false
                    }) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }

                }
            )
        }
    }
}
