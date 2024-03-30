package com.crxssed.cosmos.utils.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import com.crxssed.cosmos.data.models.AppInfo

fun AppInfo.open(context: Context) {
    val intent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
    intent?.let {
        ContextCompat.startActivity(context, it, null)
    }
}

fun AppInfo.getAppIcon(context: Context): ImageBitmap? {
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

fun AppInfo.toColour(): Color {
    return Color(colour)
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
