package com.isayevapps.clicker.screens.device.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.isayevapps.clicker.R
import com.isayevapps.clicker.screens.AddCoordinates
import com.isayevapps.clicker.screens.common.ErrorDialog
import com.isayevapps.clicker.screens.common.LoadingScreen

@Composable
fun DeviceListScreen(
    viewModel: DevicesViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var itemToDelete by rememberSaveable { mutableIntStateOf(-1) }

    val onDeviceClick: (Int) -> Unit = {
        navController.navigate(AddCoordinates(it))
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
                        viewModel.deleteDevice(itemToDelete)
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
            val devices = uiState.devices
            if (uiState.error != null)
                ErrorDialog(uiState.error.toString(), viewModel::hideErrorDialog)
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
                            DeviceItem(
                                device = devices[index],
                                onClick = onDeviceClick,
                                onDeleteClick = {
                                    itemToDelete = it
                                    showDeleteDialog = true
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(64.dp))
                        }
                    }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DeviceListScreenPreview() {
//    val deviceList = listOf(
//        Device(1, "Device1", "0.0.0.0", "test", "test"),
//        Device(2, "Device2", "0.0.0.0", "test", "test"),
//        Device(3, "Device3", "0.0.0.0", "test", "test"),
//    )
    //DeviceListScreen(modifier = Modifier.fillMaxSize())
}