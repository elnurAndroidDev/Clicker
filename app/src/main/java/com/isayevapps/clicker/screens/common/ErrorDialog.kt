package com.isayevapps.clicker.screens.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.isayevapps.clicker.R // Replace with your actual R file

@Composable
fun ErrorDialog(errorText: String, onDismiss: () -> Unit) {
    var _errorText = errorText
    if (errorText.startsWith("Network"))
        _errorText = stringResource(R.string.connection_error)
    else if (errorText == "Device not found")
        _errorText = stringResource(R.string.device_not_found)
    else if (errorText == "No wifi connection")
        _errorText = stringResource(R.string.no_wifi_connection)
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = stringResource(R.string.error_dialog_title))
        },
        text = {
            Text(text = _errorText)
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}
