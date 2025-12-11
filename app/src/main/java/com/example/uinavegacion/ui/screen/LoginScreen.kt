package com.example.uinavegacion.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.uinavegacion.R
import com.example.uinavegacion.data.local.Storage.UserPreferences
import com.example.uinavegacion.navigation.Route

import com.example.uinavegacion.ui.viewmodel.LoginViewModel
import com.example.uinavegacion.data.remote.dto.LoginResponse




//1 Lo primero que creamos en el archivo
@Composable
fun LoginScreenVm(
    vm: LoginViewModel,
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    val state by vm.loginState.collectAsStateWithLifecycle()


    // Resetear estado de login
    LaunchedEffect(state.success) {
        state.user?.let { user ->
            prefs.setLoggedIn(true)
            prefs.setUserId(user.id)
            prefs.setAdmin(user.isAdmin)



            Toast.makeText(context, "Bienvenido ${user.name ?: "Invitado"}", Toast.LENGTH_SHORT).show()

            if (user.isAdmin) {
                navController.navigate(Route.Admin.path) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            } else {
                onLoginOkNavigateHome()
            }

            vm.resetLogin()
        }
    }

    // Aquí llamas a la pantalla presentacional
    LoginScreen(
        email = state.email,
        pass = state.pass,
        emailError = state.emailError,
        passError = state.passError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onEmailChange = vm::onEmailChange,
        onPassChange = vm::onPassChange,
        onSubmit = vm::loginUser,
        onGoRegister = onGoRegister
    )
}





    //2 modificamos la funcion principal haciendo private y agregando variable y elementos dle fiormulario
    @Composable // Pantalla Login (solo navegación, sin formularios)
    private fun LoginScreen(
        //3 Modificamos estos parametros
        email: String,                                           // Campo email
        pass: String,                                            // Campo contraseña
        emailError: String?,                                     // Error de email
        passError: String?,                                      // Error de password (opcional)
        canSubmit: Boolean,                                      // Habilitar botón
        isSubmitting: Boolean,                                   // Flag loading
        errorMsg: String?,                                       // Error global (credenciales)
        onEmailChange: (String) -> Unit,                         // Handler cambio email
        onPassChange: (String) -> Unit,                          // Handler cambio password
        onSubmit: () -> Unit,                                    // Acción enviar
        onGoRegister: () -> Unit                                 // Acción ir a registro
    ) {
        val bg = MaterialTheme.colorScheme.background // Fondo distinto para contraste
        //4 Agregamos la siguiente linea
        var showPass by remember { mutableStateOf(false) }        // Estado local para mostrar/ocultar contraseña
        val painter = painterResource(id = R.drawable.login)



        Box(
            modifier = Modifier
                .fillMaxSize() // Ocupa todo
                .background(bg) // Fondo
                .padding(16.dp), // Margen
            contentAlignment = Alignment.Center // Centro
        ) {
            Column(
                //5 Anexamos el modificador
                modifier = Modifier.fillMaxWidth(),              // Ancho completo
                horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
            ) {
                Image(
                    painter = painter,
                    contentDescription = "login",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Inicio de sesion",
                    style = MaterialTheme.typography.headlineSmall // Título
                )
                Spacer(Modifier.height(12.dp)) // Separación

                Text(
                    text = "Inicia sesion para acceder a todas las funciones y difrutar del futbol total",
                    textAlign = TextAlign.Center // Alineación centrada
                )
                Spacer(Modifier.height(20.dp)) // Separación

                //5 Borramos los elementos anteriores y comenzamos a agregar los elementos dle formulario
// ---------- EMAIL ----------
                OutlinedTextField(
                    value = email,                               // Valor actual
                    onValueChange = onEmailChange,               // Notifica VM (valida email)
                    label = { Text("Email") },                   // Etiqueta
                    singleLine = true,                           // Una línea
                    isError = emailError != null,                // Marca error si corresponde
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email        // Teclado de email
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError != null) {                        // Muestra mensaje si hay error
                    Text(
                        emailError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(Modifier.height(8.dp))                    // Espacio

                // ---------- PASSWORD (oculta por defecto) ----------
                OutlinedTextField(
                    value = pass,                                // Valor actual
                    onValueChange = onPassChange,                // Notifica VM
                    label = { Text("Contraseña") },              // Etiqueta
                    singleLine = true,                           // Una línea
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(), // Toggle mostrar/ocultar
                    trailingIcon = {                             // Ícono para alternar visibilidad
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    isError = passError != null,                 // (Opcional) marcar error
                    modifier = Modifier.fillMaxWidth()           // Ancho completo
                )
                if (passError != null) {                         // (Opcional) mostrar error
                    Text(
                        passError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(Modifier.height(16.dp))                   // Espacio

                // ---------- BOTÓN ENTRAR ----------
                Button(
                    onClick = onSubmit,                          // Envía login
                    enabled = canSubmit && !isSubmitting,        // Solo si válido y no cargando
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color(0xFF2E811F))// Ancho completo
                ) {
                    if (isSubmitting) {                          // UI de carga
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Validando...")
                    } else {
                        Text("Entrar")
                    }
                }

                if (errorMsg != null) {                          // Error global (credenciales)
                    Spacer(Modifier.height(8.dp))
                    Text("credenciales incorrectas", color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(12.dp))                   // Espacio

                // ---------- BOTÓN IR A REGISTRO ----------
                OutlinedButton(
                    onClick = onGoRegister,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(
                            0xFF218D1B
                        )
                    )
                ) {
                    Text("Crear cuenta", color = Color(0xFF124933))
                }
                //fin modificacion de formulario
            }
        }
    }
