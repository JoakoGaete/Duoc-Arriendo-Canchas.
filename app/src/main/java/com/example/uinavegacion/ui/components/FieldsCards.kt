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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.uinavegacion.data.local.field.FieldEntity

import com.example.uinavegacion.R

@Composable
fun FieldCard(
    field: FieldEntity,
    onClick: () -> Unit
) {
    Card (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            Image(
                painter= painterResource(
                    id = when (field.imageUrl){
                        "cancha1" -> R.drawable.cancha1
                        "cancha2" -> R.drawable.cancha2
                        "cancha3" -> R.drawable.cancha3
                        else -> R.drawable.cancha4
                    }
                ),
                contentDescription= field.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(field.name, style = MaterialTheme.typography.titleMedium)
                Text(field.type, color = Color.DarkGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Ubicación: ${field.location}")
                Text(
                    text = "Precio: $${field.pricePerHour.toInt()} / hora",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}