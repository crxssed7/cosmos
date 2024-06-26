package com.crxssed.cosmos.utils.extensions

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.crxssed.cosmos.data.models.AppInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.TimeUnit

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

fun Context.saveSelectedApps(selectedApps: Set<AppInfo>) {
    val prefs = getSharedPreferences("selected_apps", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(selectedApps)
    editor.putString("selected_apps", json)
    editor.apply()
}

fun Context.getSelectedApps(): Set<AppInfo> {
    val prefs = getSharedPreferences("selected_apps", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = prefs.getString("selected_apps", null)
    val type = object : TypeToken<Set<AppInfo>>() {}.type
    return gson.fromJson(json, type) ?: emptySet()
}

fun Context.getAppUsageTime(packageName: String): Long {
    val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
        ?: return 0

    Log.w("Hit", "Hit")
    val currentTime = System.currentTimeMillis()
    val startTime = currentTime - TimeUnit.DAYS.toMillis(31)

    val appStats = usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY,
        startTime,
        currentTime
    )
    Log.w("Stats", appStats.size.toString())

    var totalUsageTime = 0L

    for (stats in appStats) {
        if (stats.packageName == packageName) {
            totalUsageTime += stats.totalTimeInForeground
        }
    }

    return TimeUnit.MILLISECONDS.toMinutes(totalUsageTime)
}
