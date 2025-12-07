package com.example.uinavegacion.ui.screen

import android.R
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.uinavegacion.ui.components.BookingItemAdmin
import com.example.uinavegacion.ui.viewmodel.AdminViewModel

@Composable
fun AdminScreen(adminViewModel: AdminViewModel) {
    val bookings by adminViewModel.allBookings.collectAsStateWithLifecycle()
    val fields by adminViewModel.allFields.collectAsStateWithLifecycle()
    val status by adminViewModel.status.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Campos para nueva cancha
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para seleccionar imagen desde galería
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Carga inicial de datos
    LaunchedEffect(Unit) {
        adminViewModel.loadAllBookings()
        adminViewModel.loadFields()
    }

    // Mostrar errores o status como Toast
    LaunchedEffect(status) {
        status?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            adminViewModel.clearStatus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Administración", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // --- AGREGAR CANCHA ---
        Text("Agregar nueva cancha", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Tipo") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Precio por hora") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { pickImageLauncher.launch("image/*") }, colors = ButtonDefaults.buttonColors(Color(0xFF2E811F))) {
                Text(if (imageUri == null) "Seleccionar imagen" else "Cambiar imagen")
            }
            imageUri?.let {
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagen cancha",
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                val priceDouble = price.toDoubleOrNull()
                if (name.isNotBlank() && type.isNotBlank() && location.isNotBlank() && priceDouble != null) {
                    // Llamamos al ViewModel con el nuevo método que sube cancha + imagen
                    adminViewModel.addField(
                        name = name,
                        type = type,
                        location = location,
                        pricePerHour = priceDouble,
                        imageUri = imageUri,
                        context = context
                    )
                    // Limpiar campos
                    name = ""; type = ""; location = ""; price = ""; imageUri = null
                } else {
                    Toast.makeText(context, "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(Color(0xFF2E811F))
        ) {
            Text("Agregar Cancha")
        }

        Spacer(Modifier.height(16.dp))
        Text("Canchas existentes:", fontWeight = FontWeight.Medium)
        fields.forEach { field ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                val imageUrl = field.id?.let { id ->
                    "http://10.0.2.2:8082/api/fields/$id/imagen"
                }
                imageUrl?.let { url ->
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = field.name,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("- ${field.name ?: ""} | ${field.type ?: ""} | ${field.location ?: ""} | $${field.pricePerHour ?: 0}")
            }
        }

        Spacer(Modifier.height(30.dp))
        Text("Todas las reservas", fontSize = 18.sp, fontWeight = FontWeight.Medium)

        if (bookings.isEmpty()) {
            Text("No hay reservas registradas.", color = Color.Gray)
        } else {
            bookings.forEach { reserva ->
                BookingItemAdmin(
                    reserva = reserva,
                    onDelete = { adminViewModel.deleteBooking(reserva.id ?: 0L) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}





