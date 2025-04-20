package com.isayevapps.clicker.screens.device.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isayevapps.clicker.data.db.CoordinatesDao
import com.isayevapps.clicker.data.db.DeviceDao
import com.isayevapps.clicker.data.network.ApiService
import com.isayevapps.clicker.data.network.DeleteAll
import com.isayevapps.clicker.data.network.Result
import com.isayevapps.clicker.data.network.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val apiService: ApiService,
    private val deviceDao: DeviceDao,
    private val coordinateDao: CoordinatesDao
): ViewModel() {
    private val _uiState = MutableStateFlow(DeviceListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDevices()
    }

    private fun loadDevices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            deviceDao.getAll().collect { devices ->
                _uiState.value = _uiState.value.copy(isLoading = false, devices = devices.map { it.toDevice() })
            }
        }
    }

    fun deleteDevice(deviceId: Int) {
        viewModelScope.launch {
            val device = deviceDao.getById(deviceId)
            val url = "http://${device.name}.local/api"
            val request = DeleteAll()
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = safeApiCall { apiService.deleteAll(url, request) }
            when (result) {
                is Result.Success -> {
                    deviceDao.delete(deviceId)
                    coordinateDao.deleteAllByDeviceId(deviceId)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.exception.message)
                }
            }
        }
    }

    fun hideErrorDialog() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}