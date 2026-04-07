package com.fliqu.memes.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fliqu.memes.ui.theme.DarkBg
import com.fliqu.memes.ui.theme.DarkSurface
import com.fliqu.memes.ui.theme.FliquTheme
import com.fliqu.memes.ui.theme.TealAccent
import com.fliqu.memes.ui.theme.TextPrimary
import com.fliqu.memes.ui.theme.TextSecondary
import com.fliqu.memes.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    FliquTheme {
        val selectedTab by viewModel.selectedTab.collectAsState()
        val toastMessage by viewModel.showToast.collectAsState()

        LaunchedEffect(toastMessage) {
            if (toastMessage != null) {
                delay(2000)
                viewModel.clearToast()
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
            Column(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> HomeScreen(viewModel)
                    1 -> BrowseScreen(viewModel)
                    2 -> EditorScreen(viewModel)
                    3 -> ToolsScreen(viewModel)
                    4 -> SettingsScreen(viewModel)
                }
            }

            if (toastMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp, start = 16.dp, end = 16.dp),
                    containerColor = TealAccent,
                    contentColor = Color.White
                ) {
                    Text(
                        text = toastMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .navigationBarsPadding()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isSelected = selectedTab == 0,
                        onClick = { viewModel.selectedTab.value = 0 }
                    )
                    TabItem(
                        icon = Icons.Default.Search,
                        label = "Browse",
                        isSelected = selectedTab == 1,
                        onClick = { viewModel.selectedTab.value = 1 }
                    )
                    TabItem(
                        icon = Icons.Default.Edit,
                        label = "Editor",
                        isSelected = selectedTab == 2,
                        onClick = { viewModel.selectedTab.value = 2 }
                    )
                    TabItem(
                        icon = Icons.Default.Build,
                        label = "Tools",
                        isSelected = selectedTab == 3,
                        onClick = { viewModel.selectedTab.value = 3 }
                    )
                    TabItem(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        isSelected = selectedTab == 4,
                        onClick = { viewModel.selectedTab.value = 4 }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) TealAccent else TextSecondary,
        label = "tabIconColor"
    )

    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TealAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = label,
                color = iconColor,
                fontSize = 11.sp,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
