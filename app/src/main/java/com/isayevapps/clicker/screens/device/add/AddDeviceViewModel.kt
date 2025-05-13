package com.isayevapps.clicker.screens.device.add

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isayevapps.clicker.R
import com.isayevapps.clicker.data.db.DeviceDao
import com.isayevapps.clicker.data.db.DeviceEntity
import com.isayevapps.clicker.data.network.ApiService
import com.isayevapps.clicker.data.network.Result
import com.isayevapps.clicker.data.network.retrySafeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val deviceDao: DeviceDao,
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var _uiState = MutableStateFlow(AddDeviceUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: AddDeviceEvent) {
        when (event) {
            is AddDeviceEvent.OnDeviceNameChange -> onDeviceNameChange(event.deviceName)
            is AddDeviceEvent.AddDevice -> addDevice(event.onSuccess)
            is AddDeviceEvent.HideErrorDialog -> hideErrorDialog()
            is AddDeviceEvent.OnDeviceIpChange -> onDeviceIpChange(event.deviceIp)
        }
    }

    private fun onDeviceNameChange(deviceName: TextFieldValue) {
        val invalidChar = findInvalidUrlChar(deviceName.text)
        val invalidCharText =
            invalidChar?.let { "${context.getString(R.string.invalid_character)}: \"$invalidChar\"" }
        _uiState.value = _uiState.value.copy(
            deviceName = deviceName,
            invalidUrlErrorText = invalidCharText,
            isAddEnabled = isAddEnabled()
        )
    }

    private fun onDeviceIpChange(deviceIp: TextFieldValue) {
        val isIpValid = isValidIpAddress(deviceIp.text)
        val invalidIpText = if (!isIpValid) context.getString(R.string.invalid_ip) else null
        _uiState.value = _uiState.value.copy(
            deviceIp = deviceIp,
            invalidIPErrorText = invalidIpText,
            isAddEnabled = isAddEnabled()
        )
    }

    private fun isAddEnabled() = _uiState.value.deviceName.text.isNotBlank()
            && _uiState.value.deviceIp.text.isNotBlank()
            && _uiState.value.invalidUrlErrorText == null
            && _uiState.value.invalidIPErrorText == null

    private fun addDevice(navigateBack: () -> Unit) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val ip = _uiState.value.deviceIp.text
        val deviceName = _uiState.value.deviceName.text
        val url = "http://$ip/$deviceName"
        val result = withContext(Dispatchers.IO) {
            retrySafeApiCall { apiService.login(url) }
        }
        when (result) {
            is Result.Success -> {
                withContext(Dispatchers.IO) {
                    deviceDao.insert(DeviceEntity(name = deviceName, ip = ip))
                }
                navigateBack()
            }
            is Result.Error -> {
                _uiState.value = _uiState.value.copy(error = result.exception.message, isLoading = false)
            }
        }
    }

    private fun hideErrorDialog() {
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

    private fun isValidIpAddress(ip: String): Boolean {
        if (ip.isBlank()) {
            return false
        }
        // Регулярное выражение для валидации IPv4
        val ipv4Regex = Pattern.compile(
            "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$"
        )
        return ipv4Regex.matcher(ip).matches()
    }
}