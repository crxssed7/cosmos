package com.crxssed.cosmos.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.crxssed.cosmos.data.models.AppInfo
import com.crxssed.cosmos.utils.constants.UI
import com.crxssed.cosmos.utils.extensions.getAppIcon
import com.crxssed.cosmos.utils.extensions.open
import com.crxssed.cosmos.utils.extensions.toColour

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppListItem(
    appInfo: AppInfo
) {
    val context = LocalContext.current
    val icon = appInfo.getAppIcon(context)

    Card (
        colors = CardDefaults.colors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        border = CardDefaults.border(
            border = Border(border = BorderStroke(1.dp, Color.White), shape = UI.ROUNDED),
            focusedBorder = Border(border = BorderStroke(1.dp, appInfo.toColour()), shape = UI.ROUNDED)
        ),
        scale = CardDefaults.scale(focusedScale = 0.95f),
        onClick = { appInfo.open(context) },
        modifier = Modifier
            .padding(8.dp)
            .size(width = 80.dp, height = 80.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .wrapContentHeight()
        ) {
            icon?.let {
                Image (
                    bitmap = it,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}
