package com.isayevapps.clicker.screens.coordinates.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.isayevapps.clicker.R
import com.isayevapps.clicker.screens.common.ErrorDialog
import com.isayevapps.clicker.screens.common.TimeAndClicksItem
import com.isayevapps.clicker.screens.common.TimeAndClicksUiState
import com.isayevapps.clicker.screens.coordinates.components.TimePickerDialog
import com.isayevapps.clicker.utils.timeIntToStr

@Composable
fun AddCoordinatesScreen(
    viewModel: AddCoordinateViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showTimeDialog) {
        val time = viewModel.initialTime
        TimePickerDialog(
            initialHours = time / 3600000,
            initialMinutes = (time % 3600000) / 60000,
            initialSeconds = (time % 60000) / 1000,
            initialMillis = time % 1000,
            onTimeConfirm = { hours, minutes, seconds, millis ->
                viewModel.onTimeChange(hours, minutes, seconds, millis)
                viewModel.hideTimeDialog()
            },
            onDismiss = {
                viewModel.hideTimeDialog()
            }
        )
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text(stringResource(R.string.warning)) },
            text = { Text(stringResource(R.string.sure_delete_coordinates)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.delete()
                        viewModel.hideDeleteDialog()
                    }
                ) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.hideDeleteDialog() }
                ) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
    if (uiState.error != null)
        ErrorDialog(uiState.error.toString(), viewModel::hideErrorDialog)

    AddCoordinatesContent(
        modifier = modifier,
        uiState = uiState,
        onTimeClick = { id, time ->
            viewModel.idInTimeAndClicksList = id
            viewModel.initialTime = time
            viewModel.showTimeDialog()
        },
        onClicksCountPlus = viewModel::onClicksCountPlus,
        onClicksCountMinus = viewModel::onClicksCountMinus,
        onKeyDownTimeChange = viewModel::onKeyDownTimeChange,
        onIntervalChange = viewModel::onIntervalChange,
        onStepChange = viewModel::onStepChange,
        onDecreaseX = viewModel::decreaseX,
        onIncreaseX = viewModel::increaseX,
        onDecreaseY = viewModel::decreaseY,
        onIncreaseY = viewModel::increaseY,
        onAddClick = viewModel::add,
        onDeleteClick = {
            viewModel.idInTimeAndClicksList = it
            viewModel.showDeleteDialog()
        }
    )

}

@Composable
fun AddCoordinatesContent(
    uiState: AddCoordinateUiState,
    onTimeClick: (Int, Int) -> Unit = { _, _ -> },
    onKeyDownTimeChange: (Int, String) -> Unit = { _, _ -> },
    onIntervalChange: (Int, String) -> Unit = { _, _ -> },
    onStepChange: (Int) -> Unit = {},
    onClicksCountPlus: (Int) -> Unit = {},
    onClicksCountMinus: (Int) -> Unit = {},
    onDecreaseX: () -> Unit = {},
    onIncreaseX: () -> Unit = {},
    onDecreaseY: () -> Unit = {},
    onIncreaseY: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onDeleteClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            val coordinates = uiState.coordinates
            items(coordinates.size, key = { coordinates[it].id }) {
                TimeAndClicksItem(
                    TimeAndClicksUiState(
                        id = coordinates[it].id,
                        index = coordinates[it].index,
                        time = timeIntToStr(coordinates[it].time),
                        clicksCount = coordinates[it].clicksCount,
                        onTimeClick = { onTimeClick(it, coordinates[it].time) },
                        onClicksCountPlus = { onClicksCountPlus(it) },
                        onClicksCountMinus = { onClicksCountMinus(it) },
                        onKeyDownTimeChange = { s -> onKeyDownTimeChange(it, s) },
                        onIntervalChange = { s -> onIntervalChange(it, s) },
                        onDeleteClick = { onDeleteClick(it) }
                    )
                )
            }
            if (coordinates.size < 16)
                item {
                    Button(
                        modifier = Modifier.padding(top = 16.dp),
                        onClick = {
                            onAddClick()
                        }
                    ) {
                        Text("Добавить время")
                    }
                }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${stringResource(R.string.step)}: ${uiState.step}",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Slider(
                value = uiState.step.toFloat(),
                onValueChange = { onStepChange(it.toInt()) },
                valueRange = 10f..100f,
                steps = 9,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = onDecreaseY, modifier = Modifier.size(56.dp)) {
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary),
                    tint = Color.White,
                    contentDescription = "Up"
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecreaseX, modifier = Modifier.size(56.dp)) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary),
                        tint = Color.White,
                        contentDescription = "Left"
                    )
                }
                Spacer(modifier = Modifier.width(56.dp)) // Adjust spacing as needed
                IconButton(onClick = onIncreaseX, modifier = Modifier.size(56.dp)) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary),
                        tint = Color.White,
                        contentDescription = "Right"
                    )
                }
            }

            IconButton(onClick = onIncreaseY, modifier = Modifier.size(56.dp)) {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary),
                    tint = Color.White,
                    contentDescription = "Down"
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddCoordinatesScreenPreview() {
    AddCoordinatesContent(uiState = AddCoordinateUiState(), modifier = Modifier.fillMaxSize())
}