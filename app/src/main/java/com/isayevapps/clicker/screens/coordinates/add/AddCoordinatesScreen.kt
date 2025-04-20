package com.isayevapps.clicker.screens.coordinates.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.isayevapps.clicker.R
import com.isayevapps.clicker.screens.common.AppTextField
import com.isayevapps.clicker.screens.common.ErrorDialog
import com.isayevapps.clicker.screens.coordinates.components.TimePickerDialog

@Composable
fun AddCoordinatesScreen(
    viewModel: AddCoordinateViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by rememberSaveable { mutableStateOf(false) }
    if (showDialog) {
        TimePickerDialog(
            initialHours = 0,
            initialMinutes = 0,
            initialSeconds = 0,
            onTimeConfirm = { hours, minutes, seconds ->
                viewModel.onTimeChange(hours, minutes, seconds)
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
    if (uiState.error != null)
        ErrorDialog(uiState.error.toString(), viewModel::hideErrorDialog)
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            label = stringResource(R.string.coordinate_name)
        )

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AppTextField(
                    value = uiState.time,
                    readOnly = true,
                    onValueChange = {},
                    label = stringResource(R.string.time)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDialog = true }
                        .background(Color.Transparent))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(stringResource(R.string.clicks), fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        enabled = uiState.clicksCount > 1,
                        onClick = viewModel::onClicksCountMinus
                    ) {
                        Text("—")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${uiState.clicksCount}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        enabled = uiState.clicksCount < 255,
                        onClick = viewModel::onClicksCountPlus
                    ) {
                        Text("+")
                    }
                }
            }
        }
        Row {
            AppTextField(
                value = uiState.keyDownTime.toString(),
                onValueChange = viewModel::onKeyDownTimeChange,
                label = stringResource(R.string.key_down_time),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            AppTextField(
                value = uiState.intervalTime.toString(),
                onValueChange = viewModel::onIntervalChange,
                label = "Интервал (мс)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
        Row {
            AppTextField(
                value = uiState.x.toString(),
                onValueChange = viewModel::onXChange,
                label = "X",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            AppTextField(
                value = uiState.y.toString(),
                onValueChange = viewModel::onYChange,
                label = "Y",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${stringResource(R.string.step)}: ${uiState.step}",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Slider(
                value = uiState.step.toFloat(),
                onValueChange = { viewModel.onStepChange(it.toInt()) },
                valueRange = 10f..100f,
                steps = 9,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = viewModel::decreaseY, modifier = Modifier.size(56.dp)) {
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
                IconButton(onClick = viewModel::decreaseX, modifier = Modifier.size(56.dp)) {
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
                IconButton(onClick = viewModel::increaseX, modifier = Modifier.size(56.dp)) {
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

            IconButton(onClick = viewModel::increaseY, modifier = Modifier.size(56.dp)) {
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
        Spacer(modifier = Modifier.weight(1f))
        Button(
            enabled = uiState.name.isNotBlank(),
            onClick = {
                viewModel.save {
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
private fun AddCoordinatesScreenPreview() {
//    AddCoordinatesScreen(
//        Modifier.fillMaxSize()
//    )
}