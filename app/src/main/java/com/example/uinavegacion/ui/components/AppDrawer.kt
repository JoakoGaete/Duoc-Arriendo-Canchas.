package com.example.uinavegacion.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons // Íconos Material
import androidx.compose.material.icons.filled.Home // Ícono Home
import androidx.compose.material.icons.filled.AccountCircle // Ícono Login
import androidx.compose.material.icons.filled.BookmarkAdded
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier // Modificador
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Tipo de ícono
import androidx.compose.ui.unit.dp

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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Canchas Duoc",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
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

// Helper para construir la lista estándar de ítems del drawer
@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,   // Acción Home
    onLogin: () -> Unit,  // Acción Login
    onRegister: () -> Unit, // Acción Registro
    onBooking: () -> Unit,
    onMapa: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Home", Icons.Filled.Home, onHome),          // Ítem Home
    DrawerItem("Login", Icons.Filled.AccountCircle, onLogin),       // Ítem Login
    DrawerItem("Registro", Icons.Filled.Person, onRegister), // Ítem Registro
    DrawerItem("Arrendar", Icons.Filled.BookmarkAdded, onBooking), // Item Arrendar
    DrawerItem("nuestra Ubicacion", Icons.Filled.PinDrop, onMapa) // Item Arrendar
)