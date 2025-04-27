package com.isayevapps.clicker.screens.device.add

import com.isayevapps.clicker.utils.NetworkScanner
import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isayevapps.clicker.R
import com.isayevapps.clicker.data.db.DeviceDao
import com.isayevapps.clicker.data.db.DeviceEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val deviceDao: DeviceDao,
    private val networkScanner: NetworkScanner,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var _uiState = MutableStateFlow(AddDeviceUiState())
    val uiState = _uiState.asStateFlow()

    fun onDeviceNameChange(deviceName: TextFieldValue) {
        val invalidChar = findInvalidUrlChar(deviceName.text)
        if (invalidChar != null)
            _uiState.value =
                _uiState.value.copy(invalidUrlErrorText = "${context.getString(R.string.invalid_character)}: \"$invalidChar\"")
        else
            _uiState.value = _uiState.value.copy(invalidUrlErrorText = null)
        _uiState.value = _uiState.value.copy(deviceName = deviceName)
    }

    fun addEnabled() = _uiState.value.deviceName.text.isNotBlank()
            && _uiState.value.invalidUrlErrorText == null

    fun addDevice(navigateBack: () -> Unit) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        var ip: String? = null
        try {
            ip = networkScanner.findFirstHost(_uiState.value.deviceName.text)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            return@launch
        }
        if (ip == null) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = "Device not found")
            return@launch
        }
        withContext(Dispatchers.IO) {
            deviceDao.insert(DeviceEntity(name = _uiState.value.deviceName.text, ip = ip))
        }
        navigateBack()
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