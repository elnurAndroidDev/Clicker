package com.isayevapps.clicker.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.isayevapps.clicker.R
import com.isayevapps.clicker.screens.common.ClickerTopAppBar
import com.isayevapps.clicker.screens.coordinates.add.AddCoordinateViewModel
import com.isayevapps.clicker.screens.coordinates.add.AddCoordinatesScreen
import com.isayevapps.clicker.screens.device.add.AddDeviceScreen
import com.isayevapps.clicker.screens.device.add.AddDeviceViewModel
import com.isayevapps.clicker.screens.device.list.DeviceListScreen
import com.isayevapps.clicker.screens.device.list.DevicesViewModel
import kotlinx.serialization.Serializable

@Serializable
object DeviceList

@Serializable
object AddDevice

@Serializable
data class AddCoordinates(val deviceId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(modifier: Modifier = Modifier.fillMaxSize()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val appBarText = when (currentRoute) {
        DeviceList::class.qualifiedName -> stringResource(R.string.devices)
        AddDevice::class.qualifiedName -> stringResource(R.string.add_device)
        AddCoordinates::class.qualifiedName + "/{deviceId}" -> stringResource(R.string.edit_coordinates)
        else -> ""
    }

    val onNavigationClick: (() -> Unit)? =
        if (currentRoute != DeviceList::class.qualifiedName) navController::navigateUp else null

    Scaffold(
        modifier = modifier,
        topBar = {
            ClickerTopAppBar(
                text = appBarText,
                onNavigationClick = onNavigationClick
            )
        },
        floatingActionButton = {
            when (currentRoute) {
                DeviceList::class.qualifiedName -> {
                    FloatingActionButton(
                        onClick = { navController.navigate(AddDevice) },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }

                else -> {}
            }
        }) { contentPadding ->
        NavHost(
            navController,
            startDestination = DeviceList,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable<DeviceList> {
                val viewModel = hiltViewModel<DevicesViewModel>()
                DeviceListScreen(
                    viewModel,
                    navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            composable<AddDevice> { backStackEntry ->
                val viewModel = hiltViewModel<AddDeviceViewModel>()
                AddDeviceScreen(
                    viewModel,
                    navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            composable<AddCoordinates> { backStackEntry ->
                val viewModel = hiltViewModel<AddCoordinateViewModel>()
                AddCoordinatesScreen(
                    viewModel,
                    navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
                )
            }
        }
    }

}