package com.crxssed.cosmos

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import com.crxssed.cosmos.data.models.AppInfo
import com.crxssed.cosmos.ui.components.BatteryLevel
import com.crxssed.cosmos.ui.components.CurrentTime
import com.crxssed.cosmos.ui.screens.MainScreen
import com.crxssed.cosmos.ui.screens.SettingsScreen
import com.crxssed.cosmos.ui.theme.CosmosTheme
import com.crxssed.cosmos.utils.extensions.getSelectedApps
import com.crxssed.cosmos.utils.extensions.saveSelectedApps

class MainActivity : ComponentActivity() {
    private var selectedTabIndex = mutableIntStateOf(0)
    private val tabs = listOf("Home", "Apps")

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            var selectedApps by remember { mutableStateOf(applicationContext.getSelectedApps()) }

            LaunchedEffect(selectedApps) {
                applicationContext.saveSelectedApps(selectedApps)
            }

            CosmosTheme {
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
                                    onAppChecked = { app: AppInfo, checked: Boolean ->
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
