package com.crxssed.cosmos.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.crxssed.cosmos.data.models.AppInfo
import com.crxssed.cosmos.utils.constants.UI
import com.crxssed.cosmos.utils.extensions.open
import com.crxssed.cosmos.utils.extensions.toColour

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppCheckbox(appInfo: AppInfo, checked: Boolean, onAppChecked: (AppInfo, Boolean) -> Unit) {
    var isChecked by remember {
        mutableStateOf(checked)
    }
    val context = LocalContext.current

    Row {
        Card (
            onClick = {
                isChecked = !isChecked
                onAppChecked(appInfo, isChecked)
            },
            colors = CardDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = CardDefaults.border(
                border = Border(border = BorderStroke(1.dp, Color.White), shape = UI.ROUNDED),
                focusedBorder = Border(border = BorderStroke(1.dp, appInfo.toColour()), shape = UI.ROUNDED)
            ),
            scale = CardDefaults.scale(focusedScale = 0.95f),
            shape = CardDefaults.shape(shape = UI.ROUNDED),
            modifier = Modifier
                .padding(8.dp)
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = {checked ->
                    isChecked = checked
                    onAppChecked(appInfo, isChecked)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = appInfo.toColour(),
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.focusProperties { canFocus = false }
            )
        }
        Card (
            onClick = { appInfo.open(context) },
            colors = CardDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = CardDefaults.border(
                border = Border(border = BorderStroke(1.dp, Color.White), shape = UI.ROUNDED),
                focusedBorder = Border(border = BorderStroke(1.dp, appInfo.toColour()), shape = UI.ROUNDED)
            ),
            scale = CardDefaults.scale(focusedScale = 0.99f),
            shape = CardDefaults.shape(shape = UI.ROUNDED),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            Text(
                text = appInfo.label,
                color = Color.White,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}
