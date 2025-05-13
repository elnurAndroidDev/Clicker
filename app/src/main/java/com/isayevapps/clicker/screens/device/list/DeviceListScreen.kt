package com.isayevapps.clicker.screens.device.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isayevapps.clicker.R
import com.isayevapps.clicker.screens.common.ErrorDialog
import com.isayevapps.clicker.screens.common.LoadingScreen
import com.isayevapps.clicker.screens.device.Device

@Composable
fun DeviceListScreen(
    navigateToCoordinates: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<DevicesViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val onDeviceClick = navigateToCoordinates
    val onDeviceDelete: (Device) -> Unit = {
        viewModel.itemToDelete = it
        showDeleteDialog = true
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.warning)) },
            text = { Text(stringResource(R.string.sure_delete_device)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteDevice()
                    }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
    when {
        uiState.isLoading -> LoadingScreen()
        else -> {
            if (uiState.error != null)
                ErrorDialog(uiState.error.toString(), viewModel::hideErrorDialog)
            DeviceListScreenContent(modifier, uiState.devices, onDeviceClick, onDeviceDelete)
        }
    }
}

@Composable
fun DeviceListScreenContent(
    modifier: Modifier = Modifier,
    devices: List<Device> = emptyList(),
    onDeviceClick: (Int) -> Unit = {},
    onDeviceDelete: (Device) -> Unit = {}
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (devices.isEmpty())
            Text(stringResource(R.string.no_devices), fontSize = 16.sp)
        else
            LazyColumn(
                modifier = Modifier.matchParentSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(devices.size) { index ->
                    val device = devices[index]
                    DeviceItem(
                        device = device,
                        onClick = { onDeviceClick(device.id) },
                        onDeleteClick = { onDeviceDelete(device) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DeviceListScreenPreview() {
    val deviceList = listOf(
        Device(1, "Device1", "0.0.0.0"),
        Device(2, "Device2", "0.0.0.0"),
        Device(3, "Device3", "0.0.0.0"),
    )
    DeviceListScreenContent(modifier = Modifier.fillMaxSize(), deviceList)
}