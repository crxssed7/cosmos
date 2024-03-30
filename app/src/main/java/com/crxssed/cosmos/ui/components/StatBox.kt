package com.crxssed.cosmos.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crxssed.cosmos.utils.constants.UI

@Composable
fun StatBox(colour: Color, title: String, value: String) {
    Box(modifier = Modifier
        .padding(top = 8.dp)
        .border(1.dp, colour, UI.ROUNDED)
        .padding(12.dp)
        .fillMaxWidth()) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Thin
            )
            Text(
                text = value,
                color = Color.White,
                modifier = Modifier.padding(15.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
