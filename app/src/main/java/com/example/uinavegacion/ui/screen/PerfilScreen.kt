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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.uinavegacion.ui.components.ReservaItem

import com.example.uinavegacion.ui.viewmodel.LoginViewModel
import com.example.uinavegacion.ui.viewmodel.UserBookingsViewModel

@Composable
fun PerfilScreen(
    userId: Long?,
    loginViewModel: LoginViewModel,
    userBookingsViewModel: UserBookingsViewModel
) {
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
    val bookingState by userBookingsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        if (userId != null) {
            loginViewModel.loadUserById(userId)          // debes implementar esto en LoginViewModel
            userBookingsViewModel.loadBookingsByUser(userId)
        }
    }

    val user = loginState.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loginState.isLoading) {
            CircularProgressIndicator()
            Text("Cargando perfil...")
            return@Column
        }

        loginState.errorMsg?.let {
            Text(text = it, color = Color.Red, fontSize = 18.sp)
            return@Column
        }

        user?.let {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = it.name?.take(1)?.uppercase() ?:"?",
                    fontSize = 48.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Nombre: ${it.name ?: "No registrado"}", fontSize = 20.sp)
            Text("Email: ${it.email ?:"No registrado"}", fontSize = 18.sp)
            Text("Teléfono: ${it.phone ?:"No registrado"}", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(30.dp))
            HorizontalDivider()

            Text(
                "Mis reservas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            when {
                bookingState.isLoading -> CircularProgressIndicator()
                bookingState.error != null -> Text(text = bookingState.error!!, color = Color.Red)
                bookingState.bookings.isEmpty() -> Text("No tienes reservas registradas.", color = Color.Gray)
                else -> bookingState.bookings.forEach { reserva ->
                    ReservaItem(
                        reserva = reserva,
                        onDelete = {
                            reserva.id?.let { id ->
                                userBookingsViewModel.deleteBooking(id)
                                userId?.let { userBookingsViewModel.loadBookingsByUser(it) }
                            }
                        }
                    )
                }
            }
        }
    }
}
