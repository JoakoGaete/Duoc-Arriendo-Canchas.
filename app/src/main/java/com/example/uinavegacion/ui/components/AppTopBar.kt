package com.example.uinavegacion.ui.components

import android.R
import androidx.compose.material.icons.Icons // Conjunto de íconos Material
import androidx.compose.material.icons.filled.Home // Ícono Home
import androidx.compose.material.icons.filled.AccountCircle // Ícono Login
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Menu // Ícono hamburguesa
import androidx.compose.material.icons.filled.MoreVert // Ícono 3 puntitos (overflow)
import androidx.compose.material.icons.filled.Person // Ícono Registro
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.CenterAlignedTopAppBar // TopAppBar centrada
import androidx.compose.material3.DropdownMenu // Menú desplegable
import androidx.compose.material3.DropdownMenuItem // Opción del menú
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon // Para mostrar íconos
import androidx.compose.material3.IconButton // Botones con ícono
import androidx.compose.material3.MaterialTheme // Tema Material
import androidx.compose.material3.Text // Texto
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.* // remember / mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.data.local.Storage.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable // Composable reutilizable: barra superior
fun AppTopBar(
    onOpenDrawer: () -> Unit, // Abre el drawer (hamburguesa)
    onHome: () -> Unit,       // Navega a Home
    onLogin: () -> Unit,      // Navega a Login
    onRegister: () -> Unit,    // Navega a Registro
    onBooking: () -> Unit,
    onAdmin: ()-> Unit,
    onPerfil: ()-> Unit
) {
    //lo que hace es crear una variable de estado recordada que le dice a la interfaz
    // si el menú desplegable de 3 puntitos debe estar visible (true) o oculto (false).
    var showMenu by remember { mutableStateOf(false) } // Estado del menú overflow
    val context = LocalContext.current
    val userPrefrs = remember { UserPreferences(context) }
    val isLoggedIn by  userPrefrs.isLoggedIn.collectAsStateWithLifecycle(false)
    val isAdmin by userPrefrs.isAdmin.collectAsStateWithLifecycle(false)


    CenterAlignedTopAppBar( // Barra alineada al centro
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Green
        ),
        title = { // Slot del título
            Text(
                text = "Arriendo Canchas Duoc", // Título visible
                style = MaterialTheme.typography.titleLarge,// Estilo grande
                maxLines = 1,              // asegura una sola línea Int.MAX_VALUE   // permite varias líneas
                overflow = TextOverflow.Ellipsis // agrega "..." si no cabe

            )
        },
        navigationIcon = { // Ícono a la izquierda (hamburguesa)
            IconButton(onClick = onOpenDrawer) { // Al presionar, abre drawer
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú") // Ícono
            }
        },
        actions = { // Acciones a la derecha (íconos + overflow)
            IconButton(onClick = onHome) { // Ir a Home
                Icon(Icons.Filled.Home, contentDescription = "Home")
            }
            IconButton(onClick = onBooking) {
                Icon(Icons.Filled.BookmarkAdded, contentDescription = "Arrendar")
            }
            IconButton(
                onClick = {
                    when {
                        !isLoggedIn -> onLogin()
                        isAdmin -> onAdmin()
                        else -> onPerfil()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = when {
                        !isLoggedIn -> MaterialTheme.colorScheme.outline
                        isAdmin -> Color.Yellow
                        else -> Color(0xFF1343BE)
                    }
                )
            }

        }
    )
}