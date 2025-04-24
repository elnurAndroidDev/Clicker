package com.isayevapps.clicker.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isayevapps.clicker.R


data class TimeAndClicksUiState(
    val id: Int = 0,
    val index: Int = 0,
    val clicksCount: Int = 1,
    val time: String = "00:00:00.000",
    val keyDownTime: Int = 100,
    val intervalTime: Int = 100,
    val onKeyDownTimeChange: (String) -> Unit = {},
    val onIntervalChange: (String) -> Unit = {},
    val onTimeClick: () -> Unit = {},
    val onClicksCountPlus: () -> Unit = {},
    val onClicksCountMinus: () -> Unit = {},
    val onDeleteClick: () -> Unit = {}
)

@Composable
fun TimeAndClicksItem(
    state: TimeAndClicksUiState,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color = Color.Transparent, shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(16.dp))
                        .padding(vertical = 8.dp)
                        .clickable { state.onTimeClick() }
                        .weight(1f)
                ) {
                    Text(state.time, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Button(
                        enabled = state.clicksCount > 1,
                        onClick = state.onClicksCountMinus
                    ) {
                        Text("—")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${state.clicksCount}", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        enabled = state.clicksCount < 255,
                        onClick = state.onClicksCountPlus
                    ) {
                        Text("+")
                    }
                }
            }
            Row {
                AppTextField(
                    value = state.keyDownTime.toString(),
                    onValueChange = state.onKeyDownTimeChange,
                    label = stringResource(R.string.key_down_time),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                AppTextField(
                    value = state.intervalTime.toString(),
                    onValueChange = state.onIntervalChange,
                    label = stringResource(R.string.interval),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
        IconButton(
            onClick = state.onDeleteClick,
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Delete",
                tint = Color.Red
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimeAndClicksItemPreview() {
    TimeAndClicksItem(state = TimeAndClicksUiState())
}