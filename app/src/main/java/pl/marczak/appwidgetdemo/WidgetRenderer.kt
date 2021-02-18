package pl.marczak.appwidgetdemo

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import pl.marczak.appwidgetdemo.WidgetProvider.Companion.ACTION_TOGGLE

class WidgetRenderer(private val context: Context) {

    fun render(state: WidgetViewState): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_stopwatch)

        remoteViews.setInt(R.id.appwidget_root, "setBackgroundColor", state.backgroundColor)
        remoteViews.setTextViewText(R.id.timer_name, state.name)

        val (label, icon) = when {
            state.isLoading -> {
                "Loading..." to R.drawable.ic_stopwatch_black_24
            }
            state.isRunning -> {
                "Running" to R.drawable.ic_pause
            }
            else -> {
                "Paused" to R.drawable.ic_play
            }
        }

        remoteViews.setTextViewText(R.id.timer_time, label)
        remoteViews.setImageViewResource(R.id.play_or_pause, icon)
        remoteViews.setInt(R.id.play_or_pause, "setBackgroundColor", state.backgroundColor)

        val browseStopwatchIntent = PendingIntent.getActivity(
            context, state.widgetId,
            MainActivity.createIntent(context),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val clickPendingIntent = PendingIntent.getBroadcast(
            context, state.widgetId,
            Intent(context, WidgetProvider::class.java).apply {
                action = ACTION_TOGGLE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, state.widgetId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (!state.isLoading) {
            remoteViews.setOnClickPendingIntent(R.id.play_or_pause, clickPendingIntent)
            remoteViews.setOnClickPendingIntent(R.id.appwidget_root, browseStopwatchIntent)
        }
        return remoteViews
    }
}