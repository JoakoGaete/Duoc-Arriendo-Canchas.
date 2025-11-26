package com.example.uinavegacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.uinavegacion.data.local.Storage.UserPreferences
import com.example.uinavegacion.data.repository.BookingApiRepository
import com.example.uinavegacion.data.repository.CanchasApiRepository
import com.example.uinavegacion.data.repository.UserApiRepository

import com.example.uinavegacion.navigation.AppNavGraph

import com.example.uinavegacion.ui.viewmodel.BookingViewModel
import com.example.uinavegacion.ui.viewmodel.BookingViewModelFactory
import com.example.uinavegacion.ui.viewmodel.LoginVIewModelFactory
import com.example.uinavegacion.ui.viewmodel.LoginViewModel
import com.example.uinavegacion.ui.viewmodel.UserBookingViewModelFactory
import com.example.uinavegacion.ui.viewmodel.UserBookingsViewModel
import kotlinx.coroutines.flow.firstOrNull

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        org.osmdroid.config.Configuration.getInstance().load(
            applicationContext,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}


/*
* En Compose, Surface es un contenedor visual que viene de Material 3.Crea un bloque
*  que puedes personalizar con color, forma, sombra (elevación).
Sirve para aplicar un fondo (color, borde, elevación, forma) siguiendo las guías de diseño
* de Material.
Piensa en él como una “lona base” sobre la cual vas a pintar tu UI.
* Si cambias el tema a dark mode, colorScheme.background
* cambia automáticamente y el Surface pinta la pantalla con el nuevo color.
* */
@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext

    // Repositorios de microservicio
    val bookingRepository = remember { BookingApiRepository() }
    val fieldRepository = remember { CanchasApiRepository() }
    val prefs = remember { UserPreferences(context) }


    // ViewModels con factories
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginVIewModelFactory(UserApiRepository(),prefs )
    )

    LaunchedEffect (Unit) {
        val id = prefs.userId.firstOrNull() ?: 0L
        if (prefs.isLoggedIn.firstOrNull() == true && id != 0L) {
            loginViewModel.loadUserById(id)
        }
    }

    val bookingViewModel: BookingViewModel = viewModel(
        factory = BookingViewModelFactory(bookingRepository)
    )

    val userBookingsViewModel: UserBookingsViewModel = viewModel(
        factory = UserBookingViewModelFactory(bookingRepository)
    )

    val navController = rememberNavController()

    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                loginViewModel = loginViewModel,
                bookingViewModel = bookingViewModel,
                userBookingsViewModel = userBookingsViewModel,
                bookingRepository = bookingRepository,
                fieldRepository = fieldRepository
            )
        }
    }
}


