package com.isayevapps.clicker.screens.device.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.7f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = device.name,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = device.ip,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onDeleteClick
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
        device = Device(0, "Device", "192.168.0.1"),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}