package com.isayevapps.clicker.screens.coordinates.components

import TimeWheelPicker
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.isayevapps.clicker.R

@Composable
fun TimePickerDialog(
    initialHours: Int = 0,
    initialMinutes: Int = 0,
    initialSeconds: Int = 0,
    initialMillis: Int = 0,
    onTimeConfirm: (hours: Int, minutes: Int, seconds: Int, millis: Int) -> Unit,
    onDismiss: () -> Unit
) {
    // Локальные состояния для хранения выбранного времени
    var selectedHours by remember { mutableIntStateOf(initialHours) }
    var selectedMinutes by remember { mutableIntStateOf(initialMinutes) }
    var selectedSeconds by remember { mutableIntStateOf(initialSeconds) }
    var selectedMillis by remember { mutableIntStateOf(initialMillis) }

    // Диалог с заголовком, контентом и кнопками для подтверждения или отмены
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.choose_time)) },
        text = {
            // TimeWheelPicker внутри диалога обновляет локальные состояния
            TimeWheelPicker(
                initialHours = selectedHours,
                initialMinutes = selectedMinutes,
                initialSeconds = selectedSeconds,
                initialMillis = selectedMillis,
                onTimeSelected = { hours, minutes, seconds, millis ->
                    selectedHours = hours
                    selectedMinutes = minutes
                    selectedSeconds = seconds
                    selectedMillis = millis
                }
            )
        },
        confirmButton = {
            Button(onClick = {
                onTimeConfirm(selectedHours, selectedMinutes, selectedSeconds, selectedMillis)
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
