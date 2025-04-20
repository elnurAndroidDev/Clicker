package com.isayevapps.clicker.screens.coordinates.edit

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isayevapps.clicker.data.db.CoordinateEntity
import com.isayevapps.clicker.data.db.CoordinatesDao
import com.isayevapps.clicker.data.db.DeviceDao
import com.isayevapps.clicker.data.db.DeviceEntity
import com.isayevapps.clicker.data.network.ApiService
import com.isayevapps.clicker.data.network.Dot
import com.isayevapps.clicker.data.network.Move
import com.isayevapps.clicker.data.network.Result
import com.isayevapps.clicker.data.network.safeApiCall
import com.isayevapps.clicker.utils.timeIntToStr
import com.isayevapps.clicker.utils.timeStrToInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCoordinateViewModel @Inject constructor(
    private val apiService: ApiService,
    private val deviceDao: DeviceDao,
    private val coordinatesDao: CoordinatesDao,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditCoordinateUiState())
    val uiState = _uiState.asStateFlow()
    private lateinit var device: DeviceEntity

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val url = "http://${device.name}.local/api"
            val (h, m, s) = _uiState.value.time.split(":").map { it.toInt() }
            val id = _uiState.value.id
            val index = _uiState.value.index
            val deviceId = _uiState.value.deviceId
            val x = _uiState.value.x
            val y = _uiState.value.y
            val t = _uiState.value.keyDownTime
            val i = _uiState.value.intervalTime
            val n = _uiState.value.clicksCount
            val request = Dot(index, x, y, t, i, h, m, s, n)
            val result = safeApiCall { apiService.dot(url, request) }
            when (result) {
                is Result.Success -> {
                    coordinatesDao.update(
                        CoordinateEntity(
                            id = id,
                            index = index,
                            deviceId = deviceId,
                            x = x,
                            y = y,
                            time = timeStrToInt(_uiState.value.time),
                            clicksCount = n,
                            name = _uiState.value.name,
                            keyDownTime = t,
                            intervalTime = i
                        ))
                    onSuccess()
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.exception.message)
                }
            }
        }
    }

    private suspend fun moveApiCall(x: Int, y: Int, onSuccess: () -> Unit) {
        val url = "http://${device.name}.local/api"
        val request = Move(x, y)
        val result = safeApiCall { apiService.move(url, request) }
        when (result) {
            is Result.Success -> {
                onSuccess()
            }

            is Result.Error -> {
                _uiState.value = _uiState.value.copy(error = result.exception.message)
            }
        }
    }

    fun loadCoordinate(coordinateId: Int) {
        viewModelScope.launch {
            val coordinate = coordinatesDao.get(coordinateId).toCoordinate()
            device = deviceDao.getById(coordinate.deviceId)
            moveApiCall(0, 0, onSuccess = {})
            _uiState.value = _uiState.value.copy(
                deviceId = coordinate.deviceId,
                id = coordinate.id,
                index = coordinate.index,
                name = coordinate.name,
                time = timeIntToStr(coordinate.time),
                clicksCount = coordinate.clicksCount,
                keyDownTime = coordinate.keyDownTime,
                intervalTime = coordinate.intervalTime
            )
        }
    }

    fun hideErrorDialog() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    @SuppressLint("DefaultLocale")
    fun onTimeChange(hours: Int, minutes: Int, seconds: Int) {
        _uiState.value =
            _uiState.value.copy(time = String.format("%02d:%02d:%02d", hours, minutes, seconds))
    }

    fun onKeyDownTimeChange(keyDownTime: String) {
        if (keyDownTime.isBlank()) {
            _uiState.value = _uiState.value.copy(keyDownTime = 0)
            return
        }
        if (keyDownTime.all { it.isDigit() }) {
            val keyDownTimeUShort = try {
                keyDownTime.toUShort()
            } catch (e: NumberFormatException) {
                UShort.MAX_VALUE
            }
            _uiState.value = _uiState.value.copy(keyDownTime = keyDownTimeUShort.toInt())
        }
    }

    fun onIntervalChange(interval: String) {
        if (interval.isBlank()) {
            _uiState.value = _uiState.value.copy(intervalTime = 0)
            return
        }
        if (interval.all { it.isDigit() }) {
            val intervalUShort = try {
                interval.toUShort()
            } catch (e: NumberFormatException) {
                UShort.MAX_VALUE
            }
            _uiState.value = _uiState.value.copy(intervalTime = intervalUShort.toInt())
        }
    }

    fun onXChange(x: String) = viewModelScope.launch {
        if (x.isBlank())
            moveApiCall(0, _uiState.value.y) {
                _uiState.value = _uiState.value.copy(x = 0)
            }
        if (x.all { it.isDigit() }) {
            val xUShort = try {
                x.toUShort()
            } catch (e: NumberFormatException) {
                UShort.MAX_VALUE
            }
            moveApiCall(xUShort.toInt(), _uiState.value.y) {
                _uiState.value = _uiState.value.copy(x = xUShort.toInt())
            }
        }
    }

    fun increaseX() = viewModelScope.launch {
        val x = _uiState.value.x
        val step = _uiState.value.step
        if (x + step <= UShort.MAX_VALUE.toInt())
            moveApiCall(x + step, _uiState.value.y) {
                _uiState.value = _uiState.value.copy(x = x + step)
            }
        else
            moveApiCall(UShort.MAX_VALUE.toInt(), _uiState.value.y) {
                _uiState.value = _uiState.value.copy(x = UShort.MAX_VALUE.toInt())
            }
    }

    fun decreaseX() = viewModelScope.launch {
        val x = _uiState.value.x
        val step = _uiState.value.step
        if (x - step >= 0)
            moveApiCall(x - step, _uiState.value.y) {
                _uiState.value = _uiState.value.copy(x = x - step)
            }
        else
            moveApiCall(0, _uiState.value.y) {
                _uiState.value = _uiState.value.copy(x = 0)
            }
    }

    fun onYChange(y: String) = viewModelScope.launch {
        if (y.isBlank())
            moveApiCall(_uiState.value.x, 0) {
                _uiState.value = _uiState.value.copy(y = 0)
            }
        if (y.all { it.isDigit() }) {
            val yUShort = try {
                y.toUShort()
            } catch (e: NumberFormatException) {
                UShort.MAX_VALUE
            }
            moveApiCall(_uiState.value.x, yUShort.toInt()) {
                _uiState.value = _uiState.value.copy(y = yUShort.toInt())
            }
        }
    }

    fun increaseY() = viewModelScope.launch {
        val y = _uiState.value.y
        val step = _uiState.value.step
        if (y + step <= UShort.MAX_VALUE.toInt())
            moveApiCall(_uiState.value.x, y + step) {
                _uiState.value = _uiState.value.copy(y = y + step)
            }
        else
            moveApiCall(_uiState.value.x, UShort.MAX_VALUE.toInt()) {
                _uiState.value = _uiState.value.copy(y = UShort.MAX_VALUE.toInt())
            }
    }

    fun decreaseY() = viewModelScope.launch {
        val y = _uiState.value.y
        val step = _uiState.value.step
        if (y - step >= 0)
            moveApiCall(_uiState.value.x, y - step) {
                _uiState.value = _uiState.value.copy(y = y - step)
            }
        else
            _uiState.value = _uiState.value.copy(y = 0)
    }

    fun onStepChange(step: Int) {
        _uiState.value = _uiState.value.copy(step = step)
    }

    fun onClicksCountPlus() {
        val clicksCount = _uiState.value.clicksCount
        if (clicksCount < 255)
            _uiState.value = _uiState.value.copy(clicksCount = clicksCount + 1)
    }

    fun onClicksCountMinus() {
        val clicksCount = _uiState.value.clicksCount
        if (clicksCount > 1)
            _uiState.value = _uiState.value.copy(clicksCount = clicksCount - 1)
    }
}