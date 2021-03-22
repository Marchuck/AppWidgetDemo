package pl.marczak.appwidgetdemo

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.work.WorkManager

const val NO_ID = AppWidgetManager.INVALID_APPWIDGET_ID

val Int.isValidAppWidgetId: Boolean
    get() = this != NO_ID

val Bundle?.appWidgetId: Int
    get() = this?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, NO_ID) ?: NO_ID

val Context.appWidgetManager: AppWidgetManager
    get() = AppWidgetManager.getInstance(this)

fun Context.debug(s: Any) {
    Toast.makeText(applicationContext, s.toString(), Toast.LENGTH_SHORT).show()
}

val Context.workManager: WorkManager
    get() = WorkManager.getInstance(this)