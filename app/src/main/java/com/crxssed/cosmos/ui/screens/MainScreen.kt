package com.crxssed.cosmos.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.ImmersiveList
import com.crxssed.cosmos.data.models.AppInfo
import com.crxssed.cosmos.ui.components.AppListItem
import com.crxssed.cosmos.ui.components.HeaderText
import com.crxssed.cosmos.ui.components.StatBox
import com.crxssed.cosmos.utils.constants.UI
import com.crxssed.cosmos.utils.extensions.toColour

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen(
    selectedApps: Set<AppInfo>
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(1.dp, Color.Transparent, UI.ROUNDED),
        color = Color.Transparent
    ) {
        if (selectedApps.isNotEmpty()) {
            ImmersiveList(
                listAlignment = Alignment.BottomCenter,
                background = {index: Int, _: Boolean ->
                    val appInfo = selectedApps.toList()[index]

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column {
                            HeaderText(appInfo = appInfo)
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(1) {
                                    StatBox(colour = appInfo.toColour(), title = "Launches", value = "12")
                                }
                                items(1) {
                                    StatBox(colour = appInfo.toColour(), title = "Screen Time", value = "60m")
                                }
                            }
                        }
                    }
                }
            ) {
                TvLazyRow (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    itemsIndexed(selectedApps.toList()) { index: Int, appInfo: AppInfo ->
                        Box(modifier = Modifier.immersiveListItem(index)) {
                            AppListItem(
                                appInfo = appInfo
                            )
                        }
                    }
                }
            }
        } else {
            // TODO: Show text explaining that the user has to add apps using the settings page
        }
    }
}
