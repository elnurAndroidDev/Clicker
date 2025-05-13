package com.isayevapps.clicker.screens.device.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isayevapps.clicker.data.db.CoordinatesDao
import com.isayevapps.clicker.data.db.DeviceDao
import com.isayevapps.clicker.data.network.ApiService
import com.isayevapps.clicker.data.network.DeleteAll
import com.isayevapps.clicker.data.network.Result
import com.isayevapps.clicker.data.network.retrySafeApiCall
import com.isayevapps.clicker.screens.device.Device
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
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeviceListUiState())
    val uiState = _uiState.asStateFlow()
    var itemToDelete: Device? = null

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

    fun deleteDevice() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val url = "http://${itemToDelete!!.ip}/api"
            val request = DeleteAll()
            val result =
                withContext(Dispatchers.IO) { retrySafeApiCall { apiService.deleteAll(url, request) } }
            when (result) {
                is Result.Success -> {
                    withContext(Dispatchers.IO) {
                        deviceDao.delete(itemToDelete!!.id)
                        coordinateDao.deleteAllByDeviceId(itemToDelete!!.id)
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