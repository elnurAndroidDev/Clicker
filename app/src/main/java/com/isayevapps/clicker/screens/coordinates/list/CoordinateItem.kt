package com.isayevapps.clicker.screens.coordinates.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isayevapps.clicker.R
import com.isayevapps.clicker.screens.coordinates.Coordinate
import com.isayevapps.clicker.utils.timeIntToStr

@Composable
fun CoordinateItem(
    coordinate: Coordinate,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit = {},
    onDeleteClick: (Coordinate) -> Unit = { }
) {
    OutlinedCard(
        modifier = modifier.wrapContentHeight(),
        onClick = { onClick(coordinate.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 0.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = coordinate.name,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "${stringResource(R.string.coordinates)}: (${coordinate.x}, ${coordinate.y})", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${stringResource(R.string.time)}: ${timeIntToStr(coordinate.time)}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${stringResource(R.string.clicks)}: ${coordinate.clicksCount}", fontSize = 16.sp)
            }
            Row {
                IconButton(
                    onClick = { onDeleteClick(coordinate) }
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Coordinates")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun DeviceItemPreview() {

}