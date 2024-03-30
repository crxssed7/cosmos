package com.crxssed.cosmos.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crxssed.cosmos.R
import com.crxssed.cosmos.data.models.AppInfo
import com.crxssed.cosmos.utils.constants.UI
import com.crxssed.cosmos.utils.extensions.toColour

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderText(appInfo: AppInfo) {
    val blackOps = FontFamily(
        Font(R.font.black_ops, FontWeight.Normal)
    )

    Box(modifier = Modifier.border(1.dp, appInfo.toColour(), UI.ROUNDED)) {
        Text(
            text = appInfo.label.uppercase(),
            fontFamily = blackOps,
            color = Color.White,
            fontSize = 30.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(10.dp, 20.dp)
                .fillMaxWidth()
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    animationMode = MarqueeAnimationMode.Immediately,
                    delayMillis = 1000,
                    initialDelayMillis = 3000
                )
        )
    }
}
