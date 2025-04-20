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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.isayevapps.clicker.R
import com.isayevapps.clicker.screens.common.ClickerTopAppBar
import com.isayevapps.clicker.screens.coordinates.add.AddCoordinateViewModel
import com.isayevapps.clicker.screens.coordinates.add.AddCoordinatesScreen
import com.isayevapps.clicker.screens.coordinates.edit.EditCoordinateViewModel
import com.isayevapps.clicker.screens.coordinates.edit.EditCoordinatesScreen
import com.isayevapps.clicker.screens.coordinates.list.CoordinatesListScreen
import com.isayevapps.clicker.screens.coordinates.list.CoordinatesViewModel
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
data class CoordinatesList(val deviceId: Int)

@Serializable
data class EditCoordinates(val coordinateId: Int)

@Serializable
data class AddCoordinates(val deviceId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(modifier: Modifier = Modifier.fillMaxSize()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showAddCoordinateButton = rememberSaveable { mutableStateOf(false) }

    val appBarText = when (currentRoute) {
        DeviceList::class.qualifiedName -> stringResource(R.string.devices)
        CoordinatesList::class.qualifiedName + "/{deviceId}" -> stringResource(R.string.dots)
        AddDevice::class.qualifiedName -> stringResource(R.string.add_device)
        AddCoordinates::class.qualifiedName + "/{deviceId}" -> stringResource(R.string.add_coordinates)
        EditCoordinates::class.qualifiedName + "/{coordinateId}" -> stringResource(R.string.edit_coordinates)
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

                CoordinatesList::class.qualifiedName + "/{deviceId}" -> if (showAddCoordinateButton.value) {
                    val deviceId =
                        navBackStackEntry?.arguments?.getInt("deviceId") ?: return@Scaffold
                    FloatingActionButton(
                        onClick = { navController.navigate(AddCoordinates(deviceId)) },
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
                    onDeviceClick = { navController.navigate(CoordinatesList(it)) },
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
            composable<CoordinatesList> { backStackEntry ->
                val coordinateList = backStackEntry.toRoute<CoordinatesList>()
                val viewModel = hiltViewModel<CoordinatesViewModel>()
                CoordinatesListScreen(
                    coordinateList.deviceId,
                    showAddCoordinateButton,
                    viewModel,
                    onCoordinateClick = { navController.navigate(EditCoordinates(it)) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

            }
            composable<EditCoordinates> { backStackEntry ->
                val coordinateId = backStackEntry.toRoute<EditCoordinates>().coordinateId
                val viewModel = hiltViewModel<EditCoordinateViewModel>()
                EditCoordinatesScreen(
                    coordinateId,
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
                        .padding(16.dp)
                )
            }
        }
    }

}