package pl.marczak.appwidgetdemo

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import pl.marczak.appwidgetdemo.MyWidgetProvider.Companion.appWidgetManager


class MyWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE = "ACTION_TOGGLE"
        const val NO_ID = AppWidgetManager.INVALID_APPWIDGET_ID

        val Int.isValidAppWidgetId: Boolean
            get() = this != NO_ID

        val Bundle?.appWidgetId: Int
            get() = this?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, NO_ID) ?: NO_ID

        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, MyWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val componentName = ComponentName(
                context,
                MyWidgetProvider::class.java
            )
            val widgetIDs = context.appWidgetManager.getAppWidgetIds(componentName)
            context.updateWidgets(widgetIDs)
        }

        fun newWidget(widgetId: Int) = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }

        val Context.appWidgetManager: AppWidgetManager
            get() = AppWidgetManager.getInstance(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        context.debug("onReceive ${intent.action} / ${intent.extras.printOut()}")
        val widgetId = intent.extras.appWidgetId
        if (widgetId.isValidAppWidgetId && ACTION_TOGGLE == intent.action) {
            val preferences = AppWidgetPreferences(context.applicationContext)

            runCatching {
                toggleWidget(preferences, widgetId)
            }.onSuccess {
                context.updateWidget(widgetId, preferences)
            }.onFailure {
                context.error(it, "failed onReceive")
            }
        }
        super.onReceive(context, intent)
    }

    private fun toggleWidget(
        preferences: AppWidgetPreferences,
        widgetId: Int
    ) {
        preferences.getWidget(widgetId)?.let { currentState ->
            val newState = currentState.copy(isRunning = !currentState.isRunning)
            preferences.saveWidget(newState)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        context.debug("onUpdate ${appWidgetIds.joinToString { it.toString() }}")
        context.updateWidgets(appWidgetIds)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        context.debug("onDeleted ${appWidgetIds.joinToString { it.toString() }}")
        AppWidgetPreferences(context).runCatching {
            removeWidgets(appWidgetIds)
        }.onFailure {
            context.error(it, "failed delete widget")
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        context.debug("onAppWidgetOptionsChanged")
        context.updateWidgets(intArrayOf(appWidgetId))
    }

    override fun onEnabled(context: Context) {
        context.debug("onEnabled")
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        context.debug("onDisabled")
    }

    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        context.debug("onRestored")
    }
}

private fun Bundle?.printOut(): String {
    if (this == null) return ""
    return keySet().joinToString {
        "[$it] : ${get(it)}"
    }
}
