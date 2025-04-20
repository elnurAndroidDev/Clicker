package com.isayevapps.clicker.screens.device.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isayevapps.clicker.screens.device.Device

@Composable
fun DeviceItem(
    device: Device,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit = {},
    onDeleteClick: (Int) -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier,
        onClick = { onClick(device.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = device.name,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.7f),
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { onDeleteClick(device.id) }
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Device")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun DeviceItemPreview() {
    DeviceItem(
        device = Device(0,"Device"),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}