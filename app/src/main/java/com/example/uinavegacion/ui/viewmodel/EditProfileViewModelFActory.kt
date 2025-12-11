package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uinavegacion.data.local.Storage.UserPreferences
import com.example.uinavegacion.data.repository.UserApiRepository

class EditProfileViewModelFactory(
    private val repo: UserApiRepository,
    private val userId: Long
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditProfileViewModel(repo, userId) as T
    }
}
