package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.data.local.Storage.UserPreferences
import com.example.uinavegacion.ui.components.ReservaItem
import com.example.uinavegacion.ui.viewmodel.AuthViewModel
import com.example.uinavegacion.ui.viewmodel.BookingViewModel

@Composable
fun PerfilScreen(
    userId: Long?,
    authViewModel: AuthViewModel,
    bookingViewModel: BookingViewModel
) {
    val profileState by authViewModel.profile.collectAsStateWithLifecycle()
    val bookings by bookingViewModel.userBookings.collectAsStateWithLifecycle()

    // Cargar datos del usuario + reservas al entrar
    LaunchedEffect(userId) {
        if (userId != null) {
            authViewModel.loadUserById(userId)
            bookingViewModel.loadBookingsByUser(userId)
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // LOADING
        if (profileState.isLoading) {
            CircularProgressIndicator()
            Text("Cargando perfil...")
            return
        }

        // ERROR
        profileState.errorMsg?.let {
            Text(text = it, color = Color.Red, fontSize = 18.sp)
            return
        }

        // --- PERFIL ---
        profileState.user?.let { user ->

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.take(1).uppercase(),
                    fontSize = 48.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Nombre: ${user.name}", fontSize = 20.sp)
            Text("Email: ${user.email}", fontSize = 18.sp)
            Text("Teléfono: ${user.phone}", fontSize = 18.sp)


            Spacer(modifier = Modifier.height(30.dp))

            HorizontalDivider()

            // --- RESERVAS DEL USUARIO ---
            Text(
                "Mis reservas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (bookings.isEmpty()) {
                Text(
                    "No tienes reservas registradas.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            } else {
                bookings.forEach { reserva ->
                    ReservaItem(reserva = reserva, onDelete = { bookingId ->
                        bookingViewModel.deleteBooking(bookingId)
                        // Opcional: recargar las reservas para refrescar la lista
                        userId?.let { bookingViewModel.loadBookingsByUser(it) }
                    })
                }
            }
        }
    }
}
