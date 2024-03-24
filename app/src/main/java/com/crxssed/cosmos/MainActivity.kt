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
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crxssed.cosmos.ui.theme.CosmosTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object GlobalConstants {
    const val PACKAGE_NAME = "com.crxssed.cosmos"
    val SYSTEM_APP_WHITELIST = listOf(
        "com.android.vending",
        "com.sec.android.app.myfiles"
    )
}

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            navController = rememberNavController()

            var selectedApps by remember { mutableStateOf(applicationContext.getSelectedApps()) }

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
                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(end = 10.dp)
                                            .size(20.dp)
                                    )
                                    CurrentTime()
                                    Spacer(modifier = Modifier.weight(1f))
                                    BatteryLevel()
                                }
                            }
                        }
                        NavHost(
                            navController = navController as NavHostController,
                            startDestination = "mainScreen"
                        ) {
                            composable("mainScreen") {
                                MainScreen(
                                    navController = navController,
                                    selectedApps = selectedApps
                                )
                            }
                            composable("settingsScreen") {
                                SettingsScreen(
                                    selectedApps = selectedApps,
                                    context = applicationContext,
                                    onAppChecked = { app, checked ->
                                        val updatedApps = if (checked) {
                                            selectedApps + app
                                        } else {
                                            selectedApps - app
                                        }
                                        selectedApps = updatedApps
                                        applicationContext.saveSelectedApps(selectedApps)
                                    }
                                )
                            }
                            composable("allAppsScreen") {
                                AllAppsScreen(
                                    context = applicationContext,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (!navController.navigateUp()) {
            navController.navigate("mainScreen")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(navController: NavController, selectedApps: Set<AppInfo>) {
    var black_ops = FontFamily(
        Font(R.font.black_ops, FontWeight.Normal)
    )

    var title by remember { mutableStateOf("Cosmos") }

    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = Color.Transparent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title.uppercase(),
                fontFamily = black_ops,
                color = Color.White,
                fontSize = 40.sp,
                maxLines = 1,
                modifier = Modifier
                    .padding(10.dp, 20.dp)
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        animationMode = MarqueeAnimationMode.Immediately,
                        delayMillis = 1000,
                        initialDelayMillis = 3000
                    )
                    .align(Alignment.CenterHorizontally)
            )
            AppList(
                apps = selectedApps,
                onAppClicked = {packageName: String ->
                    Log.w("Launch", "Hit")
                    val intent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
                    if (intent == null) {
                        Log.w("Launch", "Intent was null")
                    }
                    intent?.let {
                        startActivity(context, it, null)
                    }
                },
                onFocus = {newTitle: String ->
                    title = newTitle
                },
                navController = navController
            )
        }
    }
}

@Composable
fun SettingsScreen(selectedApps: Set<AppInfo>, context: Context, onAppChecked: (AppInfo, Boolean) -> Unit) {
    var installedApps = remember { context.getInstalledApps() }

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        color = Color.Transparent
    ) {
        LazyColumn {
            items(installedApps) {app: AppInfo ->
                AppCheckbox(appInfo = app, checked = selectedApps.contains(app), onAppChecked = onAppChecked)
            }
        }
    }
}

@Composable
fun AllAppsScreen(context: Context) {
    var installedApps = remember { context.getInstalledApps() }

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        color = Color.Transparent
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp)
        ) {
            itemsIndexed(installedApps.toList()) {_: Int, item: AppInfo ->
                AppListItem(
                    appInfo = item,
                    // TODO: DRY this up
                    onAppClicked = {packageName: String ->
                        Log.w("Launch", "Hit")
                        val intent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
                        if (intent == null) {
                            Log.w("Launch", "Intent was null")
                        }
                        intent?.let {
                            startActivity(context, it, null)
                        }
                    },
                    onFocus = { }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCheckbox(appInfo: AppInfo, checked: Boolean, onAppChecked: (AppInfo, Boolean) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    var isChecked by remember {
        mutableStateOf(checked)
    }

    val focusedModifier = Modifier
        .border(1.dp, Color.White, shape = RoundedCornerShape(9.dp))
    val unfocusedModifier = Modifier

    Card (
        onClick = {
            isChecked = !isChecked
            onAppChecked(appInfo, isChecked)
        },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .then(if (isFocused) focusedModifier else unfocusedModifier)
    ) {
        Row (
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = {checked ->
                    isChecked = checked
                    onAppChecked(appInfo, isChecked)
                }
            )
            Text(text = appInfo.label, color = Color.White)
        }
    }
}

data class AppInfo (
    val packageName: String,
    val label: String
)

@Composable
fun AppList(apps: Set<AppInfo>, onAppClicked: (packageName: String) -> Unit, onFocus: (title: String) -> Unit, navController: NavController) {
    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(110.dp)
                .fillMaxWidth()
        ) {
            if (apps.isEmpty()) {
                items(2) {
                    ButtonItem(
                        onClick = { navController.navigate("allAppsScreen") },
                        drawable = R.drawable.apps,
                        onFocus = onFocus,
                        title = "All Apps"
                    )
                    ButtonItem(
                        onClick = { navController.navigate("settingsScreen") },
                        drawable = R.drawable.settings,
                        onFocus = onFocus,
                        title = "Settings"
                    )
                }
            } else {
                itemsIndexed(apps.toList()) {index: Int, item: AppInfo ->
                    AppListItem(appInfo = item, onAppClicked = onAppClicked, onFocus = onFocus)
                    if (index == apps.size - 1) {
                        ButtonItem(
                            onClick = { navController.navigate("allAppsScreen") },
                            drawable = R.drawable.apps,
                            onFocus = onFocus,
                            title = "All Apps"
                        )
                        ButtonItem(
                            onClick = { navController.navigate("settingsScreen") },
                            drawable = R.drawable.settings,
                            onFocus = onFocus,
                            title = "Settings"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonItem(onClick: () -> Unit, drawable: Int, onFocus: (title: String) -> Unit, title: String) {
    var isFocused by remember { mutableStateOf(false) }

    val focusedModifier = Modifier
        .border(1.dp, Color.White, shape = RoundedCornerShape(9.dp))
    val unfocusedModifier = Modifier

    Card (
        onClick = { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .size(width = 100.dp, height = 100.dp)
            .defaultMinSize(minWidth = 100.dp, minHeight = 100.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                if (isFocused) {
                    onFocus(title)
                }
            }
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
fun AppListItem(appInfo: AppInfo, onAppClicked: (packageName: String) -> Unit, onFocus: (title: String) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val icon = getAppIcon(context, appInfo.packageName)

    val focusedModifier = Modifier
        .border(1.dp, Color.White, shape = RoundedCornerShape(9.dp))
    val unfocusedModifier = Modifier

    Card (
        onClick = { onAppClicked(appInfo.packageName) },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .size(width = 100.dp, height = 100.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                if (isFocused) {
                    onFocus(appInfo.label)
                }
            }
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
            currentLevel.value = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            delay(1000)
        }
    }

    Text(
        text = "${currentLevel.value}%",
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
            apps.add(AppInfo(packageName = packageName, label = label))
        }
    }
    return apps
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
