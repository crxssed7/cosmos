package com.crxssed.cosmos.ui.components

import android.content.Context
import android.os.BatteryManager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun BatteryLevel() {
    val batteryManager = LocalContext.current.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val currentLevel = remember {
        mutableIntStateOf(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentLevel.intValue = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            delay(1000)
        }
    }

    Text(
        text = "${currentLevel.intValue}%",
        color = Color.White,
        style = TextStyle(fontSize = 12.sp),
        fontWeight = FontWeight.Bold
    )
}