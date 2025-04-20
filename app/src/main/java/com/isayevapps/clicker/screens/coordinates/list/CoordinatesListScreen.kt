package com.isayevapps.clicker.screens.coordinates.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.isayevapps.clicker.R
import com.isayevapps.clicker.screens.common.ErrorDialog
import com.isayevapps.clicker.screens.common.LoadingScreen

@Composable
fun CoordinatesListScreen(
    deviceId: Int,
    showAddCoordinateButton: MutableState<Boolean>,
    viewModel: CoordinatesViewModel,
    onCoordinateClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(deviceId) {
        viewModel.loadCoordinates(
            deviceId = deviceId,
            showAddCoordinateButton = { showAddCoordinateButton.value = it }
        )
    }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.warning)) },
            text = { Text(stringResource(R.string.sure_delete_coordinates)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteCoordinate()
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
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                if (uiState.error != null)
                    ErrorDialog(uiState.error.toString(), viewModel::hideErrorDialog)
                val coordinates = uiState.coordinates
                if (coordinates.isEmpty())
                    Text(stringResource(R.string.no_coordinates), fontSize = 16.sp)
                else
                    LazyColumn(
                        modifier = Modifier.matchParentSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(coordinates.size) { index ->
                            CoordinateItem(
                                coordinate = coordinates[index],
                                onClick = onCoordinateClick,
                                onDeleteClick = {
                                    viewModel.coordinateToDelete = it
                                    showDeleteDialog = true
                                })
                        }
                        item {
                            Spacer(modifier = Modifier.height(64.dp))
                        }
                    }
            }
        }
    }
}

@Preview
@Composable
fun CoordinatesListScreenPreview() {
//    val coordinateList = listOf(
//        Coordinate(1u, "App Icon", Pair(100, 200), 2, 0),
//        Coordinate(1u, "App Icon", Pair(100, 200), 2, 0)
//    )
    //CoordinatesListScreen()
}