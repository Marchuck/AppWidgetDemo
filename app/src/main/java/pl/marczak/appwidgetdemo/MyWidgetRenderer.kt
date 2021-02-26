package pl.marczak.appwidgetdemo

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import pl.marczak.appwidgetdemo.MyWidgetProvider.Companion.ACTION_TOGGLE
import pl.marczak.appwidgetdemo.databinding.AppwidgetStopwatchBinding
import java.text.SimpleDateFormat
import java.util.*

class MyWidgetRenderer(private val context: Context) {

    companion object {

        private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)

        @JvmStatic
        fun lastUpdated(date: Date): String {
            return dateFormat.format(date)
        }

        @JvmStatic
        fun provideLabelAndIcon(state: WidgetViewState): Pair<String, Int> {
            return when {
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
        }

        fun bind(binding: AppwidgetStopwatchBinding, state: WidgetViewState) {
            binding.appwidgetRoot.setBackgroundColor(state.backgroundColor)
            binding.timerName.text = state.name
            val (label, icon) = provideLabelAndIcon(state)
            binding.timerTime.text = label
            binding.playOrPause.setImageResource(icon)
            binding.lastUpdated.text = lastUpdated(Date())
        }
    }

    fun render(state: WidgetViewState): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_stopwatch)

        remoteViews.setInt(R.id.appwidget_root, "setBackgroundColor", state.backgroundColor)
        remoteViews.setTextViewText(R.id.timer_name, state.name)

        remoteViews.setTextViewText(R.id.last_updated, lastUpdated(Date()))

        val (label, icon) = provideLabelAndIcon(state)

        remoteViews.setTextViewText(R.id.timer_time, label)
        remoteViews.setImageViewResource(R.id.play_or_pause, icon)
        remoteViews.setInt(R.id.play_or_pause, "setBackgroundColor", state.backgroundColor)

        val browseStopwatchIntent = PendingIntent.getActivity(
            context, state.widgetId,
            MainActivity.createIntent(context),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val clickPendingIntent = PendingIntent.getBroadcast(
            context,
            state.widgetId,
            Intent(context, MyWidgetProvider::class.java).apply {
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
