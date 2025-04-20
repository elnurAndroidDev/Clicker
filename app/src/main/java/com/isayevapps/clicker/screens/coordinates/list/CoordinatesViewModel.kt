package com.isayevapps.clicker.screens.coordinates.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isayevapps.clicker.data.db.CoordinatesDao
import com.isayevapps.clicker.data.db.DeviceDao
import com.isayevapps.clicker.data.network.ApiService
import com.isayevapps.clicker.data.network.DeleteDot
import com.isayevapps.clicker.data.network.Result
import com.isayevapps.clicker.data.network.safeApiCall
import com.isayevapps.clicker.screens.coordinates.Coordinate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoordinatesViewModel @Inject constructor(
    private val apiService: ApiService,
    private val deviceDao: DeviceDao,
    private val coordinateDao: CoordinatesDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(CoordinateListUiState())
    val uiState = _uiState.asStateFlow()
    var coordinateToDelete: Coordinate? = null

    fun loadCoordinates(deviceId: Int, showAddCoordinateButton: (Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            coordinateDao.getAllByDeviceIdFlow(deviceId).collect { coordinates ->
                _uiState.value = _uiState.value.copy(isLoading = false, coordinates = coordinates.map { it.toCoordinate() })
                showAddCoordinateButton(coordinates.size < 15)
            }
        }
    }

    fun hideErrorDialog() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun deleteCoordinate() {
        viewModelScope.launch {
            val device = deviceDao.getById(coordinateToDelete!!.deviceId)
            val url = "http://${device.name}.local/api"
            val request = DeleteDot(coordinateToDelete!!.index)
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = safeApiCall { apiService.deleteDot(url, request) }
            when (result) {
                is Result.Success -> {
                    coordinateDao.delete(coordinateToDelete!!.id)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.exception.message)
                }
            }

        }
    }
}