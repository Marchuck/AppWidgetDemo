package pl.marczak.appwidgetdemo

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import timber.log.Timber
import java.util.*


class WidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE = "ACTION_TOGGLE"
        private const val NO_ID = AppWidgetManager.INVALID_APPWIDGET_ID

        val Int.isValidAppWidgetId: Boolean
            get() = this != NO_ID

        val Bundle?.appWidgetId: Int
            get() = this?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, NO_ID) ?: NO_ID
    }

    private val Context.appWidgetManager: AppWidgetManager
        get() = AppWidgetManager.getInstance(this)

    override fun onReceive(context: Context, intent: Intent) {
        val widgetId = intent.extras.appWidgetId
        if (widgetId.isValidAppWidgetId && ACTION_TOGGLE == intent.action) {


            val newState = toggleWidget()
            context.appWidgetManager.updateAppWidget(widgetId)
        }
        super.onReceive(context, intent)
    }

    private fun Context.pushNewState(newState: StopwatchViewState) {
        val remoteViews = bindRemoteViews(this, newState)
        WidgetPreferences(this).saveWidget(newState.stopwatchId, newState)
        appWidgetManager.updateAppWidget(newState.widgetId, remoteViews)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Timber.d("onUpdate ${appWidgetIds.joinToString { it.toString() }}")
        val widgetPreferences = WidgetPreferences(context)
        for (widgetId: Int in appWidgetIds) {
            widgetPreferences.getWidgetViewState(widgetId)?.let { viewState ->
                bindWidgetUI(context, appWidgetManager, viewState)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)

    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Timber.d("onDeleted ${appWidgetIds.joinToString { it.toString() }}")
        val widgetPreferences = WidgetPreferences(context)
        for (appWidgetId: Int in appWidgetIds) {
            widgetPreferences.removeWidget(appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Timber.d("onAppWidgetOptionsChanged")
        WidgetPreferences(context).getWidgetViewState(appWidgetId)?.let { viewState ->
            bindWidgetUI(context, appWidgetManager, viewState)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Timber.d("onEnabled")
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Timber.d("onDisabled")
    }

    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        Timber.d("onRestored")
    }
}