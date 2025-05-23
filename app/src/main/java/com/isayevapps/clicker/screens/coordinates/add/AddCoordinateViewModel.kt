package com.isayevapps.clicker.screens.coordinates.add

import android.annotation.SuppressLint
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isayevapps.clicker.data.db.CoordinatesDao
import com.isayevapps.clicker.data.db.DeviceDao
import com.isayevapps.clicker.data.network.ApiService
import com.isayevapps.clicker.data.network.DeleteDot
import com.isayevapps.clicker.data.network.Dot
import com.isayevapps.clicker.data.network.Move
import com.isayevapps.clicker.data.network.Result
import com.isayevapps.clicker.data.network.retrySafeApiCall
import com.isayevapps.clicker.screens.coordinates.Coordinate
import com.isayevapps.clicker.screens.device.Device
import com.isayevapps.clicker.utils.timeStrToInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddCoordinateViewModel @Inject constructor(
    private val apiService: ApiService,
    private val deviceDao: DeviceDao,
    private val coordinatesDao: CoordinatesDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deviceId = savedStateHandle.get<Int>("deviceId") ?: 0
    private val _uiState = MutableStateFlow(AddCoordinateUiState(deviceId = deviceId))
    val uiState = _uiState.asStateFlow()
    private lateinit var device: Device
    var idInTimeAndClicksList = 0
    var initialTime = 0

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            device = withContext(Dispatchers.IO) {
                deviceDao.getById(deviceId).toDevice()
            }
            coordinatesDao.getAllByDeviceIdFlow(deviceId).collect { coordinates ->
                _uiState.value =
                    _uiState.value.copy(
                        coordinates = coordinates.map { it.toCoordinate() },
                        isLoading = false
                    )
            }
        }
    }

    fun onEvent(event: AddCoordinateEvent) {
        when (event) {
            is AddCoordinateEvent.OnAddClick -> add()
            is AddCoordinateEvent.OnDeleteClick -> onDeleteClick(event.id)
            is AddCoordinateEvent.OnDecreaseX -> decreaseX()
            is AddCoordinateEvent.OnDecreaseY -> decreaseY()
            is AddCoordinateEvent.OnIncreaseX -> increaseX()
            is AddCoordinateEvent.OnIncreaseY -> increaseY()
            is AddCoordinateEvent.OnKeyDownTimeChange -> onKeyDownTimeChange(event.id, event.time)
            is AddCoordinateEvent.OnIntervalChange -> onIntervalChange(event.id, event.time)
            is AddCoordinateEvent.OnClicksCountMinus -> onClicksCountMinus(event.id)
            is AddCoordinateEvent.OnClicksCountPlus -> onClicksCountPlus(event.id)
            is AddCoordinateEvent.OnStepChange -> onStepChange(event.step)
            is AddCoordinateEvent.OnTimeChange -> onTimeChange(
                event.hours,
                event.minutes,
                event.seconds,
                event.millis
            )
            is AddCoordinateEvent.OnHideDeleteDialog -> hideDeleteDialog()
            is AddCoordinateEvent.OnHideTimeDialog -> hideTimeDialog()
            is AddCoordinateEvent.OnDelete -> delete()
            is AddCoordinateEvent.OnHideErrorDialog -> hideErrorDialog()
            is AddCoordinateEvent.OnTimeClick -> onTimeClick(event.id, event.time)
        }
    }

    private fun add() = viewModelScope.launch {
        val coordinates = _uiState.value.coordinates
        var index = 0
        for (coordinate in coordinates) {
            if (coordinate.index != index)
                break
            index++
        }
        val time = "00:00:00.000"
        val x = _uiState.value.x
        val y = _uiState.value.y
        val t = 100
        val i = 100
        val n = 1
        val coordinate = Coordinate(0, index, deviceId, x, y, n, timeStrToInt(time), t, i)
        send(coordinate)
    }

    private fun onDeleteClick(id: Int) {
        idInTimeAndClicksList = id
        showDeleteDialog()
    }

    private fun onTimeClick(id: Int, time: Int) {
        idInTimeAndClicksList = id
        initialTime = time
        showTimeDialog()
    }

    private fun delete() = viewModelScope.launch {
        val coordinate = _uiState.value.coordinates[idInTimeAndClicksList]
        val url = "http://${device.ip}/api"
        val request = DeleteDot(coordinate.index)
        val result =
            withContext(Dispatchers.IO) { retrySafeApiCall { apiService.deleteDot(url, request) } }
        when (result) {
            is Result.Success -> {
                withContext(Dispatchers.IO) {
                    coordinatesDao.delete(coordinate.id)
                }
            }

            is Result.Error -> {
                _uiState.value = _uiState.value.copy(error = result.exception.message)
            }
        }
        hideDeleteDialog()
    }

    private suspend fun send(coordinate: Coordinate) {
        val url = "http://${device.ip}/api"
        val h = coordinate.time / 3600000
        val m = (coordinate.time % 3600000) / 60000
        val s = (coordinate.time % 60000) / 1000
        val ms = coordinate.time % 1000
        val index = coordinate.index
        val x = coordinate.x
        val y = coordinate.y
        val t = coordinate.keyDownTime
        val i = coordinate.intervalTime
        val n = coordinate.clicksCount
        val request = Dot(index, x, y, t, i, h, m, s, ms, n)
        val result =
            withContext(Dispatchers.IO) { retrySafeApiCall { apiService.dot(url, request) } }
        when (result) {
            is Result.Success -> {
                withContext(Dispatchers.IO) {
                    coordinatesDao.insert(coordinate.toCoordinateEntity())
                }
            }

            is Result.Error -> {
                _uiState.value = _uiState.value.copy(error = result.exception.message)
            }
        }
    }

    private suspend fun moveApiCall(x: Int, y: Int) {
        val url = "http://${device.ip}/api"
        val request = Move(x, y)
        val result =
            withContext(Dispatchers.IO) { retrySafeApiCall { apiService.move(url, request) } }
        when (result) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(x = x, y = y)
            }

            is Result.Error -> {
                _uiState.value = _uiState.value.copy(error = result.exception.message)
            }
        }
    }

    private fun hideErrorDialog() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun showTimeDialog() {
        _uiState.value = _uiState.value.copy(showTimeDialog = true)
    }

    private fun hideTimeDialog() {
        _uiState.value = _uiState.value.copy(showTimeDialog = false)
    }

    private fun showDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }

    private fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }

    @SuppressLint("DefaultLocale")
    private fun onTimeChange(h: Int, m: Int, s: Int, ms: Int) = viewModelScope.launch {
        val coordinate = _uiState.value.coordinates[idInTimeAndClicksList]
        send(coordinate.copy(time = h * 3600000 + m * 60000 + s * 1000 + ms))
        hideTimeDialog()
    }

    private fun onKeyDownTimeChange(id: Int, keyDownTime: TextFieldValue) = viewModelScope.launch {
        val coordinate = _uiState.value.coordinates[id]
        if (keyDownTime.text.isBlank()) {
            send(coordinate.copy(keyDownTime = 0))
            return@launch
        }
        if (keyDownTime.text.all { it.isDigit() }) {
            val keyDownTimeUShort = try {
                keyDownTime.text.toUShort()
            } catch (e: NumberFormatException) {
                UShort.MAX_VALUE
            }
            send(coordinate.copy(keyDownTime = keyDownTimeUShort.toInt()))
        }
    }

    private fun onIntervalChange(id: Int, interval: TextFieldValue) = viewModelScope.launch {
        val coordinate = _uiState.value.coordinates[id]
        if (interval.text.isBlank()) {
            send(coordinate.copy(intervalTime = 0))
            return@launch
        }
        if (interval.text.all { it.isDigit() }) {
            val intervalUShort = try {
                interval.text.toUShort()
            } catch (e: NumberFormatException) {
                UShort.MAX_VALUE
            }
            send(coordinate.copy(intervalTime = intervalUShort.toInt()))
        }
    }

    private fun increaseX() = viewModelScope.launch {
        val x = _uiState.value.x
        val step = _uiState.value.step
        if (x + step <= UShort.MAX_VALUE.toInt())
            moveApiCall(x + step, _uiState.value.y)
        else
            moveApiCall(0 + step, _uiState.value.y)
    }

    private fun decreaseX() = viewModelScope.launch {
        val x = _uiState.value.x
        val step = _uiState.value.step
        if (x - step >= UShort.MIN_VALUE.toInt())
            moveApiCall(x - step, _uiState.value.y)
        else
            moveApiCall(UShort.MAX_VALUE.toInt() - step, _uiState.value.y)
    }

    private fun increaseY() = viewModelScope.launch {
        val y = _uiState.value.y
        val step = _uiState.value.step
        if (y + step <= UShort.MAX_VALUE.toInt())
            moveApiCall(_uiState.value.x, y + step)
        else
            moveApiCall(_uiState.value.x, 0 + step)
    }

    private fun decreaseY() = viewModelScope.launch {
        val y = _uiState.value.y
        val step = _uiState.value.step
        if (y - step >= UShort.MIN_VALUE.toInt())
            moveApiCall(_uiState.value.x, y - step)
        else
            moveApiCall(_uiState.value.x, UShort.MAX_VALUE.toInt() - step)
    }

    private fun onStepChange(step: Int) {
        _uiState.value = _uiState.value.copy(step = step)
    }

    private fun onClicksCountPlus(id: Int) = viewModelScope.launch {
        val coordinate = _uiState.value.coordinates[id]
        if (coordinate.clicksCount < 255)
            send(coordinate.copy(clicksCount = coordinate.clicksCount + 1))
    }

    private fun onClicksCountMinus(id: Int) = viewModelScope.launch {
        val coordinate = _uiState.value.coordinates[id]
        if (coordinate.clicksCount > 1)
            send(coordinate.copy(clicksCount = coordinate.clicksCount - 1))
    }
}