package com.example.uinavegacion.navigation
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope // Alcance de corrutina
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.data.local.Storage.UserPreferences

import com.example.uinavegacion.data.repository.BookingApiRepository
import com.example.uinavegacion.data.repository.CanchasApiRepository


import com.example.uinavegacion.ui.components.AppTopBar // Barra superior
import com.example.uinavegacion.ui.components.DrawerContent
import com.example.uinavegacion.ui.screen.AdminScreen


import com.example.uinavegacion.ui.screen.HomeScreen // Pantalla Home
import com.example.uinavegacion.ui.screen.LoginScreenVm // Pantalla Login
import com.example.uinavegacion.ui.screen.RegisterScreenVm // Pantalla Registro
import com.example.uinavegacion.ui.screen.BookingScreen
import com.example.uinavegacion.ui.screen.MapaScreen
import com.example.uinavegacion.ui.screen.PerfilScreen
import com.example.uinavegacion.ui.viewmodel.AdminViewModel
import com.example.uinavegacion.ui.viewmodel.AdminViewModelFactory


import com.example.uinavegacion.ui.viewmodel.BookingViewModel
import com.example.uinavegacion.ui.viewmodel.CanchasViewModel
import com.example.uinavegacion.ui.viewmodel.LoginViewModel
import com.example.uinavegacion.ui.viewmodel.UserBookingsViewModel


@Composable
fun AppNavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    bookingViewModel: BookingViewModel,
    userBookingsViewModel: UserBookingsViewModel,
    bookingRepository: BookingApiRepository,
    fieldRepository: CanchasApiRepository
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val isLoggedIn by prefs.isLoggedIn.collectAsStateWithLifecycle(false)
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
    val userId = loginState.user?.id

    // Funciones de navegación
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    val goBooking: () -> Unit = { navController.navigate(Route.Booking.path) }
    val goMapa: () -> Unit = { navController.navigate(Route.Mapa.path) }
    val goProfile: () -> Unit = { navController.navigate(Route.Perfil.path) }
    val goAdmin: () -> Unit = { navController.navigate(Route.Admin.path) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                isLoggedIn = isLoggedIn,
                onHome = { scope.launch { drawerState.close() }; goHome() },
                onLogin = { scope.launch { drawerState.close() }; goLogin() },
                onRegister = { scope.launch { drawerState.close() }; goRegister() },
                onLogout = {
                    scope.launch {
                        drawerState.close()
                        prefs.setLoggedIn(false)
                        loginViewModel.resetLogin()
                        goHome()
                    }
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

                // --- Home Screen ---
                composable(Route.Home.path) {
                    HomeScreen(
                        onGoBooking = goBooking,
                        canchasviewModel = remember { CanchasViewModel() },
                        onGoMapa = goMapa
                    )
                }

                // --- Login Screen ---
                composable(Route.Login.path) {
                    LoginScreenVm(
                        vm = loginViewModel,
                        onLoginOkNavigateHome = goHome,
                        onGoRegister = { navController.navigate(Route.Register.path) },
                        navController = navController
                    )
                }

                // --- Register Screen ---
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = loginViewModel,
                        onRegisteredNavigateLogin = { navController.navigate(Route.Login.path) },
                        onGoLogin = { navController.navigate(Route.Login.path) }
                    )
                }

                // --- Booking Screen ---
                composable(Route.Booking.path) {
                    if (userId != null) {
                        BookingScreen(
                            vm = bookingViewModel,
                            canchasVM = remember { CanchasViewModel() },
                            onSubmitSuccess = { navController.navigate(Route.Home.path) }
                        )
                    } else {
                        // Usuario encontrado, ir a BookingScreen
                        BookingScreen(
                            vm = bookingViewModel,
                            canchasVM = remember { CanchasViewModel() },
                            onSubmitSuccess = {
                                navController.navigate(Route.Home.path) {
                                    popUpTo(Route.Home.path) { inclusive = true }
                                }
                            }
                        )
                    }
                }


                // --- Mapa Screen ---
                    composable(Route.Mapa.path) { MapaScreen() }

                    // --- Perfil Screen ---
                    composable(Route.Perfil.path) {
                        val userId by prefs.userId.collectAsStateWithLifecycle(null)
                        val adminFlag by prefs.isAdmin.collectAsStateWithLifecycle(false)

                        if (adminFlag) {
                            // Usando ViewModel con factory para AdminScreen
                            val adminViewModel: AdminViewModel = viewModel(
                                factory = AdminViewModelFactory(
                                    application = LocalContext.current.applicationContext as android.app.Application,
                                    bookingRepository = bookingRepository,
                                    fieldRepository = fieldRepository
                                )
                            )
                            AdminScreen(adminViewModel = adminViewModel)
                        } else {
                            PerfilScreen(
                                userId = userId,
                                loginViewModel = loginViewModel,
                                userBookingsViewModel = userBookingsViewModel
                            )
                        }
                    }

                    // --- Admin Screen ---
                    composable(Route.Admin.path) {
                        val adminViewModel: AdminViewModel = viewModel(
                            factory = AdminViewModelFactory(
                                application = LocalContext.current.applicationContext as android.app.Application,
                                bookingRepository = bookingRepository,
                                fieldRepository = fieldRepository
                            )
                        )
                        AdminScreen(adminViewModel = adminViewModel)
                    }
                }
            }
        }
    }







