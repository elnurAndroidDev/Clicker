package com.isayevapps.clicker.screens.device.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isayevapps.clicker.data.db.CoordinatesDao
import com.isayevapps.clicker.data.db.DeviceDao
import com.isayevapps.clicker.data.network.ApiService
import com.isayevapps.clicker.data.network.DeleteAll
import com.isayevapps.clicker.data.network.Result
import com.isayevapps.clicker.data.network.retrySafeApiCall
import com.isayevapps.clicker.utils.NetworkScanner
import com.isayevapps.clicker.utils.NoWifiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val apiService: ApiService,
    private val deviceDao: DeviceDao,
    private val coordinateDao: CoordinatesDao,
    private val networkScanner: NetworkScanner
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeviceListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDevices()
    }

    private fun loadDevices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            deviceDao.getAll().collect { devices ->
                _uiState.value =
                    _uiState.value.copy(isLoading = false, devices = devices.map { it.toDevice() })
            }
        }
    }

    fun deleteDevice(deviceId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            var device = deviceDao.getById(deviceId)
            var isIPActual = false
            try {
                isIPActual = networkScanner.checkHost(device.ip, device.name, true)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: NoWifiException) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                return@launch
            }
            if (!isIPActual) {
                var ip: String? = null
                try {
                    ip = networkScanner.findFirstHost(device.name)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                    return@launch
                }
                if (ip == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Device not found")
                    return@launch
                }
                device = device.copy(ip = ip)
            }
            val url = "http://${device.ip}/api"
            val request = DeleteAll()
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result =
                withContext(Dispatchers.IO) { retrySafeApiCall { apiService.deleteAll(url, request) } }
            when (result) {
                is Result.Success -> {
                    withContext(Dispatchers.IO) {
                        deviceDao.delete(deviceId)
                        coordinateDao.deleteAllByDeviceId(deviceId)
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false)
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
}