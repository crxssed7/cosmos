package com.crxssed.cosmos.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CurrentTime() {
    val currentTime = remember { mutableStateOf(Calendar.getInstance().time) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = Calendar.getInstance().time
            delay(1000)
        }
    }

    val formattedTime = remember(currentTime.value) {
        SimpleDateFormat("h:mma", Locale.getDefault()).format(currentTime.value)
    }

    Text(
        text = formattedTime.uppercase(),
        color = Color.White,
        style = TextStyle(fontSize = 12.sp),
        fontWeight = FontWeight.Bold
    )
}