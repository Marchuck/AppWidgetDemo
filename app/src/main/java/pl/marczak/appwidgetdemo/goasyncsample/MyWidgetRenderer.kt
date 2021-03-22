package pl.marczak.appwidgetdemo.goasyncsample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import pl.marczak.appwidgetdemo.MainActivity
import pl.marczak.appwidgetdemo.R
import pl.marczak.appwidgetdemo.goasyncsample.MyWidgetProvider.Companion.ACTION_COUNTDOWN
import pl.marczak.appwidgetdemo.databinding.AppwidgetStopwatchBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyWidgetRenderer(private val context: Context) {

    companion object {

        private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)

        @JvmStatic
        fun lastUpdated(date: Date): String {
            return dateFormat.format(date)
        }

        fun bind(binding: AppwidgetStopwatchBinding, stateMy: MyWidgetViewState) {
            binding.appwidgetRoot.setBackgroundColor(stateMy.backgroundColor)
            binding.timerName.text = stateMy.name
            binding.lastUpdated.text = lastUpdated(Date())
        }
    }

    fun render(
        state: MyWidgetViewState,
        startTime: Long = 0
    ): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_stopwatch)

        remoteViews.setInt(R.id.appwidget_root, "setBackgroundColor", state.backgroundColor)
        remoteViews.setTextViewText(
            R.id.timer_name,
            "${state.name} ${if (state.isRunning) "(*)" else ""}"
        )

        remoteViews.setTextViewText(R.id.last_updated, lastUpdated(Date()))
        remoteViews.setTextViewText(R.id.time, renderTime(startTime, Date().time))
        remoteViews.setViewVisibility(
            R.id.progress,
            if (state.isLoading) View.VISIBLE else View.INVISIBLE
        )

        val browseStopwatchIntent = PendingIntent.getActivity(
            context, state.widgetId,
            MainActivity.createIntent(context),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val clickPendingIntent = PendingIntent.getBroadcast(
            context,
            state.widgetId,
            Intent(context, MyWidgetProvider::class.java).apply {
                action = ACTION_COUNTDOWN
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, state.widgetId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (!state.isLoading) {
            remoteViews.setOnClickPendingIntent(R.id.timer_name, clickPendingIntent)
            remoteViews.setOnClickPendingIntent(R.id.appwidget_root, browseStopwatchIntent)
        }
        return remoteViews
    }

    private fun renderTime(startTime: Long, time: Long): CharSequence {
        val total = time - startTime
        val secs = TimeUnit.MILLISECONDS.toSeconds(total)
        val ms = total - secs * 1000
        return String.format("%2d:%d", secs, ms)
    }
}
