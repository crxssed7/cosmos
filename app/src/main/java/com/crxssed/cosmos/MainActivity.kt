package com.crxssed.cosmos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.ImmersiveList
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import com.crxssed.cosmos.ui.theme.CosmosTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var selectedTabIndex = mutableIntStateOf(0)
    private val tabs = listOf("Home", "Settings")

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            var selectedApps by remember { mutableStateOf(applicationContext.getSelectedApps()) }

            LaunchedEffect(selectedApps) {
                applicationContext.saveSelectedApps(selectedApps)
            }

            CosmosTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color(15, 1, 18)) {
                    Column {
                        Box (modifier = Modifier
                            .background(Color(30, 30, 30, 0))
                            .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(end = 10.dp)
                                            .size(20.dp)
                                    )
                                    CurrentTime()
                                    Spacer(modifier = Modifier.weight(1f))
                                    TabRow(
                                        selectedTabIndex = selectedTabIndex.intValue,
                                        indicator = { tabPositions, doesTabRowHaveFocus ->
                                            TabRowDefaults.UnderlinedIndicator(
                                                currentTabPosition = tabPositions[selectedTabIndex.intValue],
                                                doesTabRowHaveFocus = doesTabRowHaveFocus,
                                            )
                                        }
//                                        modifier = Modifier.focusRestorer()
                                    ) {
                                        tabs.forEachIndexed { index, tab ->
                                            key(index) {
                                                Tab(
                                                    selected = index == selectedTabIndex.intValue,
                                                    onFocus = { selectedTabIndex.intValue = index },
                                                    colors = TabDefaults.underlinedIndicatorTabColors(),
                                                    modifier = Modifier.focusProperties { canFocus = false }
                                                ) {
                                                    Text(
                                                        text = tab,
                                                        fontSize = 12.sp,
                                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    BatteryLevel()
                                }
                            }
                        }
                        when (selectedTabIndex.intValue) {
                            0 -> {
                                MainScreen(
                                    selectedApps = selectedApps
                                )
                            }
                            1 -> {
                                SettingsScreen(
                                    selectedApps = selectedApps,
                                    context = applicationContext,
                                    onAppChecked = { app, checked ->
                                        selectedApps = if (checked) {
                                            selectedApps + app
                                        } else {
                                            selectedApps.filter { it.packageName != app.packageName }.toSet()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("onBackPressed is deprecated")
    override fun onBackPressed() {
        return
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        event ?: return super.onKeyDown(keyCode, null)

        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BUTTON_L1 -> {
                    if (selectedTabIndex.intValue == 0) {
                        selectedTabIndex.intValue = tabs.size - 1
                    } else {
                        selectedTabIndex.intValue -= 1
                    }
                }
                KeyEvent.KEYCODE_BUTTON_R1 -> {
                    if (selectedTabIndex.intValue == tabs.size - 1) {
                        selectedTabIndex.intValue = 0
                    } else {
                        selectedTabIndex.intValue += 1
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen(
    selectedApps: Set<AppInfo>
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(1.dp, Color.Transparent, RoundedCornerShape(8.dp)),
        color = Color.Transparent
    ) {
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
                                StatBox(colour = appInfo.colour, title = "Launches", value = "12")
                            }
                            items(1) {
                                StatBox(colour = appInfo.colour, title = "Screen Time", value = "60m")
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
    }
}

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
                border = Border(border = BorderStroke(1.dp, Color.White), shape = RoundedCornerShape(8.dp)),
                focusedBorder = Border(border = BorderStroke(1.dp, Color(appInfo.colour)), shape = RoundedCornerShape(8.dp))
            ),
            scale = CardDefaults.scale(focusedScale = 0.95f),
            shape = CardDefaults.shape(shape = RoundedCornerShape(8.dp)),
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
                    checkedColor = Color(appInfo.colour),
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
                border = Border(border = BorderStroke(1.dp, Color.White), shape = RoundedCornerShape(8.dp)),
                focusedBorder = Border(border = BorderStroke(1.dp, Color(appInfo.colour)), shape = RoundedCornerShape(8.dp))
            ),
            scale = CardDefaults.scale(focusedScale = 0.99f),
            shape = CardDefaults.shape(shape = RoundedCornerShape(8.dp)),
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

data class AppInfo (
    val packageName: String,
    val label: String,
    val colour: Int
)

fun AppInfo.open(context: Context) {
    val intent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent == null) {
        Log.w("Launch", "Intent was null")
    }
    intent?.let {
        startActivity(context, it, null)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppListItem(
    appInfo: AppInfo
) {
    val context = LocalContext.current
    val icon = getAppIcon(context, appInfo.packageName)

    Card (
        colors = CardDefaults.colors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        border = CardDefaults.border(
            border = Border(border = BorderStroke(1.dp, Color.White), shape = RoundedCornerShape(8.dp)),
            focusedBorder = Border(border = BorderStroke(1.dp, Color(appInfo.colour)), shape = RoundedCornerShape(8.dp))
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

private fun drawableToImageBitmap(drawable: Drawable): ImageBitmap {
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
        style = TextStyle(fontSize = 12.sp),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun BatteryLevel() {
    val batteryManager = LocalContext.current.getSystemService(BATTERY_SERVICE) as BatteryManager
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

fun Context.getInstalledApps(): List<AppInfo> {
    val apps = mutableListOf<AppInfo>()
    val packageManager = packageManager
    val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
    for (packageInfo in packages) {
        if (packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null) {
            val label = packageInfo.applicationInfo.loadLabel(packageManager).toString()
            val packageName = packageInfo.packageName

            val icon = packageInfo.applicationInfo.loadIcon(packageManager)
            val bitmap = icon.toBitmap()
            val dominantColour = bitmap.dominantColour()

            apps.add(AppInfo(packageName = packageName, label = label, colour = dominantColour))
        }
    }
    return apps
}

fun Bitmap.dominantColour(): Int {
    return Palette.from(this).generate().dominantSwatch?.rgb ?: Color.Black.toArgb()
}

fun Context.saveSelectedApps(selectedApps: Set<AppInfo>) {
    val prefs = getSharedPreferences("selected_apps", MODE_PRIVATE)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(selectedApps)
    editor.putString("selected_apps", json)
    editor.apply()
}

fun Context.getSelectedApps(): Set<AppInfo> {
    val prefs = getSharedPreferences("selected_apps", MODE_PRIVATE)
    val gson = Gson()
    val json = prefs.getString("selected_apps", null)
    val type = object : TypeToken<Set<AppInfo>>() {}.type
    return gson.fromJson(json, type) ?: emptySet()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderText(appInfo: AppInfo) {
    val blackOps = FontFamily(
        Font(R.font.black_ops, FontWeight.Normal)
    )

    Box(modifier = Modifier.border(1.dp, Color(appInfo.colour), RoundedCornerShape(8.dp))) {
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

@Composable
fun StatBox(colour: Int, title: String, value: String) {
    Box(modifier = Modifier
        .padding(top = 8.dp)
        .border(1.dp, Color(colour), RoundedCornerShape(8.dp))
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
