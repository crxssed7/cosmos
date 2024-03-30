package com.crxssed.cosmos.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import com.crxssed.cosmos.data.models.AppInfo
import com.crxssed.cosmos.ui.components.AppCheckbox
import com.crxssed.cosmos.utils.extensions.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SettingsScreen(selectedApps: Set<AppInfo>, context: Context, onAppChecked: (AppInfo, Boolean) -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            installedApps = context.getInstalledApps()
        }
        isLoading = false
    }

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        color = Color.Transparent
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            TvLazyColumn {
                itemsIndexed(installedApps) {_: Int, app: AppInfo ->
                    AppCheckbox(appInfo = app, checked = selectedApps.contains(app), onAppChecked = onAppChecked)
                }
            }
        }
    }
}
