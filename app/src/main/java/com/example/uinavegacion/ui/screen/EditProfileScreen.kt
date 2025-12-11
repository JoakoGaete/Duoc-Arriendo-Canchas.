package com.example.uinavegacion.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uinavegacion.ui.viewmodel.EditProfileViewModel

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel
) {
    val state = viewModel.uiState
    var showConfirm by remember { mutableStateOf(false) }

    // Paleta de colores
    val appGreen = Color(0xFF4CAF50)
    val appGray = Color(0xFF9E9E9E)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Editar Perfil",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = appGreen
        )

        Spacer(Modifier.height(25.dp))

        // Nombre
        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.nameError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = appGreen,
                focusedLabelColor = appGreen
            )
        )
        if (state.nameError != null) {
            Text(state.nameError!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(Modifier.height(15.dp))

        // Email
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.emailError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = appGreen,
                focusedLabelColor = appGreen
            )
        )
        if (state.emailError != null) {
            Text(state.emailError!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(Modifier.height(15.dp))

        // Teléfono
        OutlinedTextField(
            value = state.phone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.phoneError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = appGreen,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = appGreen,
                unfocusedLabelColor = Color.Gray,
                cursorColor = appGreen
            )
        )
        if (state.phoneError != null) {
            Text(state.phoneError!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(Modifier.height(15.dp))

        // Password
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Nueva contraseña (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.passwordError != null,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = appGreen,
                focusedLabelColor = appGreen,
                cursorColor = appGreen
            )
        )
        if (state.passwordError != null) {
            Text(state.passwordError!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(Modifier.height(30.dp))

        // Botón Guardar
        Button(
            onClick = { showConfirm = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = appGreen,
                contentColor = Color.White
            )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Guardar cambios")
            }
        }
    }

    // Dialog de confirmación
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Confirmar cambios") },
            text = { Text("¿Seguro que deseas guardar los cambios del perfil?") },

            confirmButton = {
                TextButton (onClick = {
                    showConfirm = false
                    viewModel.validateAndSubmit(
                        onSuccess = { navController.popBackStack() },
                        onError = { Log.e("EditProfile", it) }
                    )
                }) {
                    Text("Confirmar", color = appGreen)
                }
            },

            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Cancelar", color = appGray)
                }
            }
        )
    }
}


