package com.crxssed.cosmos.ui.components

import android.content.Context
import android.os.BatteryManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crxssed.cosmos.R
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

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${currentLevel.intValue}%",
            color = batteryColour(currentLevel.intValue, batteryManager.isCharging),
            style = TextStyle(fontSize = 12.sp),
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(id = drawableFor(currentLevel.intValue)),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(20.dp)
        )
    }
}

private fun batteryColour(batteryLevel: Int, isCharging: Boolean): Color {
    if (isCharging) { return Color.Green }

    if (batteryLevel <= 15) { return Color.Red }
    if (batteryLevel == 100) { return Color.Green }
    return Color.White
}

private fun drawableFor(batteryLevel: Int): Int {
    if (batteryLevel > 75) { return R.drawable.battery_full }
    if (batteryLevel > 50) { return R.drawable.battery_three_quarters }
    if (batteryLevel > 25) { return R.drawable.battery_half }
    if (batteryLevel > 10) { return R.drawable.battery_quarter }
    return R.drawable.battery_empty
}