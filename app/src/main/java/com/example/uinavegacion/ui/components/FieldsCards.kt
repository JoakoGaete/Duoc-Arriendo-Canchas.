package com.example.uinavegacion.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest


import com.example.uinavegacion.R
import com.example.uinavegacion.data.remote.dto.CanchasDto

@Composable
fun FieldCard(
    field: CanchasDto,
    onClick: () -> Unit
) {
    val imageUrl = "http://10.0.2.2:8082/api/fields/${field.id}/imagen"

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .error(R.drawable.cancha4)   // imagen por defecto si falla
                        .placeholder(R.drawable.cancha1) // mientras carga
                        .build()
                ),
                contentDescription = field.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(field.name ?: "Sin nombre", style = MaterialTheme.typography.titleMedium)
                Text(field.type ?: "Sin tipo", color = Color.DarkGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Ubicación: ${field.location ?: "Sin ubicación"}")
                Text(
                    text = "Precio: $${field.pricePerHour.toInt()} / hora",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
