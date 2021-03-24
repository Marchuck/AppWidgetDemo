package pl.marczak.appwidgetdemo.viewflipper

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import pl.marczak.appwidgetdemo.appWidgetId
import pl.marczak.appwidgetdemo.appWidgetManager
import pl.marczak.appwidgetdemo.isValidAppWidgetId
import java.util.*


class FlipperProvider : AppWidgetProvider() {

    companion object {

        fun newWidget(widgetId: Int) = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val renderer = FlipperRenderer(context)
        for (id in appWidgetIds) {
            val remoteViews = renderer.render("Widget #$id")
            appWidgetManager.updateAppWidget(id, remoteViews)
        }
    }
}
