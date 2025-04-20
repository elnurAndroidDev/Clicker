package com.isayevapps.clicker.screens.device.add

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isayevapps.clicker.R
import com.isayevapps.clicker.data.db.DeviceDao
import com.isayevapps.clicker.data.db.DeviceEntity
import com.isayevapps.clicker.data.network.ApiService
import com.isayevapps.clicker.data.network.Login
import com.isayevapps.clicker.data.network.Result
import com.isayevapps.clicker.data.network.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val deviceDao: DeviceDao,
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var _uiState = MutableStateFlow(AddDeviceUiState())
    val uiState = _uiState.asStateFlow()

    fun onDeviceNameChange(deviceName: String) {
        val invalidChar = findInvalidUrlChar(deviceName)
        if (invalidChar != null)
            _uiState.value =
                _uiState.value.copy(invalidUrlErrorText = "${context.getString(R.string.invalid_character)}: \"$invalidChar\"")
        else
            _uiState.value = _uiState.value.copy(invalidUrlErrorText = null)
        _uiState.value = _uiState.value.copy(deviceName = deviceName)
    }

    fun addEnabled() = _uiState.value.deviceName.isNotBlank()
            && _uiState.value.invalidUrlErrorText == null

    fun addDevice(navigateBack: () -> Unit) {
        viewModelScope.launch {
            val url = "http://${_uiState.value.deviceName}.local/api"
            val request = Login()
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = safeApiCall { apiService.login(url, request) }
            when (result) {
                is Result.Success -> {
                    deviceDao.insert(DeviceEntity(name = _uiState.value.deviceName))
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    navigateBack()
                }

                is Result.Error -> {
                    _uiState.value =
                        _uiState.value.copy(isLoading = false, error = result.exception.message)
                }
            }
        }
    }

    fun hideErrorDialog() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun findInvalidUrlChar(hostname: String): Char? {
        // Диапазоны букв и цифр
        val lowercase = 'a'..'z'
        val uppercase = 'A'..'Z'
        val digits = '0'..'9'
        val allowedChars: List<Char> = lowercase + uppercase + digits
        return hostname.firstOrNull { it !in allowedChars }
    }
}