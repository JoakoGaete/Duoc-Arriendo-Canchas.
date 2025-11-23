package com.example.uinavegacion.navigation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding // Para aplicar innerPadding
import androidx.compose.material3.Scaffold // Estructura base con slots
import androidx.compose.runtime.Composable // Marcador composable
import androidx.compose.ui.Modifier // Modificador
import androidx.navigation.NavHostController // Controlador de navegación
import androidx.navigation.compose.NavHost // Contenedor de destinos
import androidx.navigation.compose.composable // Declarar cada destino
import kotlinx.coroutines.launch // Para abrir/cerrar drawer con corrutinas

import androidx.compose.material3.ModalNavigationDrawer // Drawer lateral modal
import androidx.compose.material3.rememberDrawerState // Estado del drawer
import androidx.compose.material3.DrawerValue // Valores (Opened/Closed)
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope // Alcance de corrutina
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.data.local.Storage.UserPreferences
import com.example.uinavegacion.data.local.database.AppDatabase
import com.example.uinavegacion.data.repository.UserRepository


import com.example.uinavegacion.ui.components.AppTopBar // Barra superior
import com.example.uinavegacion.ui.components.AppDrawer // Drawer composable
import com.example.uinavegacion.ui.components.DrawerContent
import com.example.uinavegacion.ui.screen.AdminScreen

import com.example.uinavegacion.ui.viewmodel.BookingScreenVm
import com.example.uinavegacion.ui.screen.HomeScreen // Pantalla Home
import com.example.uinavegacion.ui.screen.LoginScreenVm // Pantalla Login
import com.example.uinavegacion.ui.screen.RegisterScreenVm // Pantalla Registro
import com.example.uinavegacion.ui.screen.BookingScreen
import com.example.uinavegacion.ui.screen.MapaScreen
import com.example.uinavegacion.ui.screen.PerfilScreen
import com.example.uinavegacion.ui.viewmodel.AdminViewModel
import com.example.uinavegacion.ui.viewmodel.AuthViewModel

import com.example.uinavegacion.ui.viewmodel.BookingViewModel


@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    bookingViewModel: BookingViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val isLoggedIn by prefs.isLoggedIn.collectAsStateWithLifecycle(false)

    // Helpers de navegación
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    val goBooking: () -> Unit = { navController.navigate(Route.Booking.path) }
    val goMapa: () -> Unit = { navController.navigate(Route.Mapa.path) }
    val goProfile: () -> Unit = { navController.navigate(Route.Perfil.path) }
    val goAdmin: () -> Unit = { navController.navigate(Route.Admin.path) }

    // Repositorio para AdminViewModel
    val userRepository = remember {
        UserRepository(
            userDao = AppDatabase.getInstance(context).userDao(),
            bookingDao = AppDatabase.getInstance(context).bookingDao(),
            fieldDao = AppDatabase.getInstance(context).fieldDao()
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                isLoggedIn = isLoggedIn,
                onHome = { scope.launch { drawerState.close() }; goHome() },
                onLogin = { scope.launch { drawerState.close() }; goLogin() },
                onRegister = { scope.launch { drawerState.close() }; goRegister() },
                onLogout = {
                    scope.launch { drawerState.close()
                    prefs.setLoggedIn(false)
                        authViewModel.clearLoginState()
                    goHome()}
                },
                onBooking = { scope.launch { drawerState.close() }; goBooking() },
                onMapa = { scope.launch { drawerState.close() }; goMapa() },
                onProfile = { scope.launch { drawerState.close() }; goProfile() }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister,
                    onBooking = goBooking,
                    onMapa = goMapa,
                    onPerfil = goProfile
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Home.path) {
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoBooking = goBooking,
                        viewModel = authViewModel,
                        onGoMapa = goMapa
                    )
                }

                composable(Route.Login.path) {
                    LoginScreenVm(
                        vm = authViewModel,
                        onLoginOkNavigateHome = {
                            // Revisar si es admin
                            val user = authViewModel.login.value.user
                            if (user?.isAdmin == true) goAdmin() else goHome()
                        },
                        onGoRegister = goRegister,
                        navController = navController
                    )
                }

                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                composable(Route.Booking.path) {
                    val loginState by authViewModel.login.collectAsStateWithLifecycle()
                    val userId = loginState.user?.id ?: 0L
                    BookingScreenVm(
                        vm = bookingViewModel,
                        userId = userId,
                        onBookingSuccess = goHome
                    )
                }

                composable(Route.Mapa.path) { MapaScreen() }

                composable(Route.Perfil.path) {
                    val userId by prefs.userId.collectAsStateWithLifecycle(null)
                    val adminFlag by prefs.isAdmin.collectAsStateWithLifecycle(false)

                    if (adminFlag) {
                        val adminViewModel = remember { AdminViewModel(userRepository) }
                        AdminScreen(adminViewModel = adminViewModel)
                    } else {
                        PerfilScreen(
                            userId = userId,
                            authViewModel = authViewModel,
                            bookingViewModel = bookingViewModel
                        )
                    }
                }

                // Nueva ruta Admin
                composable(Route.Admin.path) {
                    val adminViewModel = remember { AdminViewModel(userRepository) }
                    AdminScreen(adminViewModel = adminViewModel)
                }
            }
        }
    }
}







