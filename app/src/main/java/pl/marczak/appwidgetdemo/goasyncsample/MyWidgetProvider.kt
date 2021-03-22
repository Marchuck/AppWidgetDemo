package pl.marczak.appwidgetdemo.goasyncsample

import android.appwidget.AppWidgetManager
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


class MyWidgetProvider : BroadcastReceiver() {

    companion object {
        const val ACTION_COUNTDOWN = "ACTION_COUNTDOWN"

        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, MyWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val componentName = ComponentName(
                context,
                MyWidgetProvider::class.java
            )
            val widgetIDs = context.appWidgetManager.getAppWidgetIds(componentName)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIDs)
            context.sendBroadcast(intent)
        }

        fun newWidget(widgetId: Int) = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
    }

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val preferences = MyWidgetPreferences(context)
        val renderer = MyWidgetRenderer(context)

        val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
        if (ids != null) {
            ids.forEach { id ->
                context.onReceive(preferences, renderer, id, action)
            }
        } else {
            val widgetId = intent.extras.appWidgetId.takeIf { it.isValidAppWidgetId } ?: return
            context.onReceive(preferences, renderer, widgetId, action)
        }
    }

    private fun Context.onReceive(
        preferences: MyWidgetPreferences,
        renderer: MyWidgetRenderer,
        widgetId: Int,
        action: String
    ) {
        val currentState = preferences.getWidget(widgetId) ?: return

        when (action) {
            AppWidgetManager.ACTION_APPWIDGET_DELETED -> {
                preferences.removeWidget(widgetId)
            }
            AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED,
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val remoteViews = renderer.render(currentState.copy(toggleDate = Date().time))
                appWidgetManager.updateAppWidget(widgetId, remoteViews)
            }
            ACTION_COUNTDOWN -> {
                val state = toggleWidget(preferences, widgetId) ?: return
                theFinalCountdown(state)
            }
        }
    }

    private fun Context.theFinalCountdown(
        state: MyWidgetViewState
    ) {
        val pendingResult: PendingResult? = goAsync()
        val startTime = Date().time
        val interval = 100L

        var elapsedInterval = interval

        val renderer = MyWidgetRenderer(this)

        val runnable = object : Runnable {
            override fun run() {
                elapsedInterval += interval
                val shouldEnd = elapsedInterval > 7_000L

                appWidgetManager.updateAppWidget(
                    state.widgetId,
                    renderer.render(state.copy(isLoading = !shouldEnd), startTime)
                )
                if (shouldEnd) {
                    handler.removeCallbacks(this)
                    pendingResult?.finish()
                } else {
                    handler.postDelayed(this, interval)
                }
            }
        }
        handler.postDelayed(runnable, interval)
    }

    private fun toggleWidget(
        preferences: MyWidgetPreferences,
        widgetId: Int
    ): MyWidgetViewState? {
        return preferences.getWidget(widgetId)?.let { currentState ->
            val newState = currentState.copy(isRunning = !currentState.isRunning)
            preferences.saveWidget(newState)
            newState
        }
    }
}

fun Context.updateWidget(id: Int) {
    MyWidgetPreferences(this).getWidget(id)?.let { state ->
        appWidgetManager.updateAppWidget(
            state.widgetId,
            MyWidgetRenderer(this).render(state)
        )
    }
}
