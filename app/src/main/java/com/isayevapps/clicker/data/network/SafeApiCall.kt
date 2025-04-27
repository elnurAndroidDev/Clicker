package com.isayevapps.clicker.data.network

import kotlinx.coroutines.delay

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

// Функция safeApiCall для безопасного выполнения запросов
suspend fun <T> retrySafeApiCall(
    maxRetries: Int = 10,
    retryDelayMillis: Long = 100,
    apiCall: suspend () -> T
): Result<T> {
    repeat(maxRetries) {
        try {
            val response = apiCall()
            if (response != null) {
                return Result.Success(response)
            }
        } catch (e: Exception) {
            Result.Error(Exception("Network error"))
        }
        delay(retryDelayMillis)
    }
    return Result.Error(Exception("Network error"))
}

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T> {
    return try {
        val response = apiCall()
        Result.Success(response)
    } catch (e: Exception) {
        Result.Error(Exception("Network error"))
    }
}