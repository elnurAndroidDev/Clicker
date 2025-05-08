package com.isayevapps.clicker.screens.device.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.isayevapps.clicker.R
import com.isayevapps.clicker.screens.common.AppTextField
import com.isayevapps.clicker.screens.common.ErrorDialog
import com.isayevapps.clicker.screens.common.LoadingScreen

@Composable
fun AddDeviceScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<AddDeviceViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    when {
        uiState.isLoading -> LoadingScreen()
        else -> AddDeviceContent(
            modifier,
            uiState,
            viewModel::onEvent,
            navigateBack
        )
    }

}

@Composable
fun AddDeviceContent(
    modifier: Modifier = Modifier,
    uiState: AddDeviceUiState = AddDeviceUiState(),
    onEvent: (AddDeviceEvent) -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    if (uiState.error != null)
        ErrorDialog(
            errorText = uiState.error.toString(),
            onDismiss = { onEvent(AddDeviceEvent.HideErrorDialog) })

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppTextField(
            value = uiState.deviceName,
            onValueChange = { onEvent(AddDeviceEvent.OnDeviceNameChange(it)) },
            errorText = uiState.invalidUrlErrorText,
            label = stringResource(R.string.device_name),
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            enabled = uiState.isAddEnabled,
            onClick = {
                onEvent(AddDeviceEvent.AddDevice(onSuccess = navigateBack))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.add),
                fontSize = 16.sp,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddDeviceScreenPreview() {
    AddDeviceContent(Modifier.fillMaxSize())
}