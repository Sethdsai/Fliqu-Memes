package com.fliqu.memes.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fliqu.memes.ui.theme.FliquTheme
import com.fliqu.memes.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val toastMessage by viewModel.showToast.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    FliquTheme(darkTheme = isDarkTheme) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavItem.values().forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selectedTab == index,
                            onClick = { viewModel.selectTab(index) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                when (selectedTab) {
                    0 -> HomeScreen(viewModel)
                    1 -> BrowseScreen(viewModel)
                    2 -> EditorScreen(viewModel)
                    3 -> ToolsScreen(viewModel)
                    4 -> SettingsScreen(viewModel)
                }

                toastMessage?.let { message ->
                    LaunchedEffect(message) {
                        kotlinx.coroutines.delay(2000)
                        viewModel.clearToast()
                    }
                    Snackbar(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(message)
                    }
                }
            }
        }
    }
}

private enum class NavItem(val icon: ImageVector, val label: String) {
    HOME(Icons.Default.Home, "Home"),
    BROWSE(Icons.Default.Search, "Browse"),
    EDITOR(Icons.Default.Edit, "Editor"),
    TOOLS(Icons.Default.Build, "Tools"),
    SETTINGS(Icons.Default.Settings, "Settings")
}
