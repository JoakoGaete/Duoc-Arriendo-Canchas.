package com.example.uinavegacion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.remote.dto.CanchasDto
import com.example.uinavegacion.data.repository.CanchasApiRepository
import kotlinx.coroutines.launch

data class FieldsUiState(
    val isLoading: Boolean = false,
    val fields: List<CanchasDto> = emptyList(),
    val error: String? = null,
    val lastActionMessage: String? = null
)

class CanchasViewModel(
    private val repository: CanchasApiRepository = CanchasApiRepository()
) : ViewModel() {

    var uiState by mutableStateOf(FieldsUiState())
        private set


    val fields = uiState.fields ?: emptyList()

    fun loadFields() {
        uiState = uiState.copy(isLoading = true, error = null, lastActionMessage = null)

        viewModelScope.launch {
            val result = repository.fetchCanchas()

            uiState = result.fold(
                onSuccess = { data ->
                    uiState.copy(
                        isLoading = false,
                        fields = data
                    )
                },
                onFailure = { e ->
                    uiState.copy(
                        isLoading = false,
                        error = e.message ?: "Error desconocido"
                    )
                }
            )
        }
    }

    fun createField(
        name: String,
        type: String,
        location: String,
        pricePerHour: Double,
        imageUrl: String
    ) {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {

            val newField = CanchasDto(
                id = 0L,
                name = name,
                type = type,
                location = location,
                pricePerHour = pricePerHour,
                imageUrl = imageUrl
            )

            val result = repository.create(newField)

            uiState = result.fold(
                onSuccess = { created ->

                    val updated = uiState.fields + created

                    uiState.copy(
                        isLoading = false,
                        fields = updated,
                        lastActionMessage = "Cancha creada id=${created.name}"
                    )
                },
                onFailure = { e ->
                    uiState.copy(
                        isLoading = false,
                        error = e.message ?: "Error al crear cancha"
                    )
                }
            )
        }
    }
}

