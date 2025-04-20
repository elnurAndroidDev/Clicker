package com.isayevapps.clicker.screens.device.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.navigation.NavController
import com.isayevapps.clicker.R
import com.isayevapps.clicker.screens.common.AppTextField
import com.isayevapps.clicker.screens.common.ErrorDialog
import com.isayevapps.clicker.screens.common.LoadingScreen

@Composable
fun AddDeviceScreen(
    viewmodel: AddDeviceViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val uiState by viewmodel.uiState.collectAsState()
    when {
        uiState.isLoading -> LoadingScreen()
        else -> AddDeviceContent(uiState, viewmodel, navController, modifier)
    }

}

@Composable
fun AddDeviceContent(
    uiState: AddDeviceUiState,
    viewmodel: AddDeviceViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    if (uiState.error != null)
        ErrorDialog(errorText = uiState.error.toString(), onDismiss = viewmodel::hideErrorDialog)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppTextField(
            value = uiState.deviceName,
            onValueChange = viewmodel::onDeviceNameChange,
            errorText = uiState.invalidUrlErrorText,
            label = stringResource(R.string.device_name),
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            enabled = viewmodel.addEnabled(),
            onClick = {
                viewmodel.addDevice {
                    navController.navigateUp()
                }
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
    //AddDeviceScreen(Modifier.fillMaxSize())
}