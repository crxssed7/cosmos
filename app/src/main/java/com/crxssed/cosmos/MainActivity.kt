package com.crxssed.cosmos

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.crxssed.cosmos.ui.theme.CosmosTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CosmosTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color(15, 1, 18)) {
                    Column {
                        Box (modifier = Modifier
                            .background(Color(30, 30, 30, 0))
                            .fillMaxWidth()) {
                            Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                    CurrentTime()
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(text = "cosmos", color = Color.White, style = TextStyle(fontSize = 12.sp))
                                }
                            }
                        }
                        MainContainer()
                    }
                }
            }
        }
    }
}

@Composable
fun MainContainer() {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        color = Color.Transparent
    ) {
        AppList(
            apps = listOf(
                AppInfo("org.xbmc.kodi", "Kodi"),
                AppInfo("com.magneticchen.daijishou", "Daijishō")
            ),
            onAppClicked = {packageName: String ->
                Log.w("Launch", "Hit")
                val intent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
                if (intent == null) {
                    Log.w("Launch", "Intent was null")
                }
                intent?.let {
                    startActivity(context, it, null)
                }
            }
        )
    }
}

data class AppInfo (
    val package_name: String,
    val label: String
)

@Composable
fun AppList(apps: List<AppInfo>, onAppClicked: (packageName: String) -> Unit) {
    Box (contentAlignment = Alignment.Center, modifier = Modifier
        .fillMaxHeight()) {
        LazyRow (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            if (apps.isEmpty()) {
                items(1) {
                    ButtonItem(
                        onClick = { /*TODO*/ },
                        drawable = R.drawable.settings
                    )
                }
            } else {
                itemsIndexed(apps) {index: Int, item: AppInfo ->
                    AppListItem(appInfo = item, onAppClicked = onAppClicked)
                    if (index == apps.size - 1) {
                        ButtonItem(
                            onClick = { /*TODO*/ },
                            drawable = R.drawable.settings
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonItem(onClick: () -> Unit, drawable: Int) {
    var isFocused by remember { mutableStateOf(false) }

    val focusedModifier = Modifier
        .size(width = 105.dp, height = 105.dp)
        .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
    val unfocusedModifier = Modifier.size(width = 100.dp, height = 100.dp)

    Card (
        onClick = { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .then(if (isFocused) focusedModifier else unfocusedModifier)
    ) {
        Box (
            modifier = Modifier
                .padding(4.dp)
                .background(Color(30, 30, 30))
                .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(8.dp))
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Image (
                    painter = painterResource(id = drawable),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListItem(appInfo: AppInfo, onAppClicked: (packageName: String) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val icon = getAppIcon(context, appInfo.package_name)

    val focusedModifier = Modifier
        .size(width = 105.dp, height = 105.dp)
        .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
    val unfocusedModifier = Modifier.size(width = 100.dp, height = 100.dp)

    Card (
        onClick = { onAppClicked(appInfo.package_name) },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .then(if (isFocused) focusedModifier else unfocusedModifier)
    ) {
        Box (
            modifier = Modifier
                .padding(4.dp)
                .background(Color(30, 30, 30))
                .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(8.dp))
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
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
}

fun getAppIcon(context: Context, packageName: String): ImageBitmap? {
    val packageManager: PackageManager = context.packageManager
    return try {
        val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
        val drawable: Drawable = packageManager.getApplicationIcon(applicationInfo)
        drawableToImageBitmap(drawable)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }
}

private fun drawableToImageBitmap(drawable: Drawable): ImageBitmap? {
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
}

fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx/2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width , y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

@Composable
fun CurrentTime() {
    val currentTime = remember { mutableStateOf(Calendar.getInstance().time) }

    // Update current time every minute
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = Calendar.getInstance().time
            delay(1000) // Update every minute
        }
    }

    val formattedTime = remember(currentTime.value) {
        SimpleDateFormat("h:mma", Locale.getDefault()).format(currentTime.value)
    }

    Text(
        text = formattedTime.uppercase(),
        color = Color.White,
        style = TextStyle(fontSize = 12.sp)
    )
}
