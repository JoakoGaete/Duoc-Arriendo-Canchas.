package com.example.uinavegacion.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons // Íconos Material
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home // Ícono Home
import androidx.compose.material.icons.filled.AccountCircle // Ícono Login
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person // Ícono Registro
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon // Ícono en ítem del drawer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem // Ítem seleccionable
import androidx.compose.material3.NavigationDrawerItemDefaults // Defaults de estilo
import androidx.compose.material3.Text // Texto
import androidx.compose.material3.ModalDrawerSheet // Contenedor de contenido del drawer
import androidx.compose.runtime.Composable // Marcador composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier // Modificador
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Tipo de ícono
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.data.local.Storage.UserPreferences

// Pequeña data class para representar cada opción del drawer
data class DrawerItem( // Estructura de un ítem de menú lateral
    val label: String, // Texto a mostrar
    val icon: ImageVector, // Ícono del ítem
    val onClick: () -> Unit // Acción al hacer click
)

@Composable // Componente Drawer para usar en ModalNavigationDrawer
fun AppDrawer(
    currentRoute: String?, // Ruta actual (para marcar seleccionado si quieres)
    items: List<DrawerItem>, // Lista de ítems a mostrar
    modifier: Modifier = Modifier // Modificador opcional
) {
    ModalDrawerSheet( // Hoja que contiene el contenido del drawer
        modifier = modifier // Modificador encadenable
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = "Icono balon",
                tint = Color(0xFF219149),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Canchas Duoc",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF078632)
            )
        }
        // Recorremos las opciones y pintamos ítems
        items.forEach { item -> // Por cada ítem
            NavigationDrawerItem( // Ítem con estados Material
                label = { Text(item.label) }, // Texto visible
                selected = false, // Puedes usar currentRoute == ... si quieres marcar
                onClick = item.onClick, // Acción al pulsar
                icon = { Icon(item.icon, contentDescription = item.label) }, // Ícono
                modifier = Modifier, // Sin mods extra
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = Color.Blue,) // Estilo por defecto
            )
        }
    }
}
@Composable
fun DrawerContent(
    isLoggedIn: Boolean,
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onLogout: () -> Unit,
    onBooking: () -> Unit,
    onMapa: () -> Unit,
    onProfile: () -> Unit,

) {
    val context = LocalContext.current
    val prefs = remember(context) { UserPreferences(context) }

    val isLoggedIn by prefs.isLoggedIn
        .collectAsStateWithLifecycle(initialValue = false)

    val items =
        if (isLoggedIn)
            drawerItemsLoggedIn(
                onHome = onHome,
                onBooking = onBooking,
                onMapa = onMapa,
                onProfile = onProfile,
                onLogout = onLogout
            )
        else
            drawerItemsLoggedOut(
                onHome = onHome,
                onLogin = onLogin,
                onRegister = onRegister,
                onMapa = onMapa
            )

    AppDrawer(currentRoute = null, items = items)
}
fun drawerItemsLoggedOut(
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onMapa: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Inicio", Icons.Filled.Home, onHome),
    DrawerItem("Iniciar sesión", Icons.Filled.AccountCircle, onLogin),
    DrawerItem("Registro", Icons.Filled.Person, onRegister),
    DrawerItem("Ubicación", Icons.Filled.PinDrop, onMapa)
)

fun drawerItemsLoggedIn(
    onHome: () -> Unit,
    onLogout: () -> Unit,
    onBooking: () -> Unit,
    onMapa: () -> Unit,
    onProfile: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Inicio", Icons.Filled.Home, onHome),
    DrawerItem("Mi Perfil", Icons.Filled.AccountCircle, onProfile),
    DrawerItem("Arrendar", Icons.Filled.BookmarkAdded, onBooking),
    DrawerItem("Ubicación", Icons.Filled.PinDrop, onMapa),
    DrawerItem("Cerrar sesión", Icons.AutoMirrored.Filled.ExitToApp, onLogout)
)
