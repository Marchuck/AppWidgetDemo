package pl.marczak.appwidgetdemo.pokesample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import pl.marczak.appwidgetdemo.R
import pl.marczak.appwidgetdemo.pokesample.PokedexWidgetProvider.Companion.ACTION_NEXT
import pl.marczak.appwidgetdemo.pokesample.PokedexWidgetProvider.Companion.ACTION_PREV
import java.util.*

class PokedexRenderer(private val context: Context) {

    fun render(state: PokedexViewState): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_pokedex)

        when (state) {
            is PokedexViewState.Poke -> {
                remoteViews.setTextViewText(R.id.poke_name, "#${state.id}\n${state.name}")

                val image = state.image
                if (image != null) {
                    remoteViews.setImageViewBitmap(R.id.poke_image, image.bitmap)
                }

                remoteViews.setOnClickPendingIntent(
                    R.id.poke_prev,
                    clickPendingIntent(
                        widgetId = state.widgetId,
                        targetId = state.id,
                        action = ACTION_PREV
                    )
                )
                remoteViews.setOnClickPendingIntent(
                    R.id.poke_next,
                    clickPendingIntent(
                        widgetId = state.widgetId,
                        targetId = state.id,
                        action = ACTION_NEXT
                    )
                )
            }
            is PokedexViewState.Loading -> {
                remoteViews.setTextViewText(R.id.poke_name, "#${state.id}\nLoading...")
                val click = clickPendingIntent(
                    widgetId = state.widgetId,
                    targetId = state.id,
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                )
                remoteViews.setOnClickPendingIntent(R.id.poke_image, click)
                remoteViews.setOnClickPendingIntent(R.id.poke_name, click)
            }
            is PokedexViewState.Error -> {
                remoteViews.setTextViewText(
                    R.id.poke_name,
                    "Failed to load #${state.id}\ntap to retry"
                )
                val pendingIntent = clickPendingIntent(
                    widgetId = state.widgetId,
                    targetId = state.id,
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                )
                remoteViews.setOnClickPendingIntent(R.id.poke_image, pendingIntent)
                remoteViews.setOnClickPendingIntent(R.id.poke_name, pendingIntent)
            }
        }
        return remoteViews
    }

    private fun clickPendingIntent(
        widgetId: Int,
        targetId: Int,
        action: String
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            Objects.hash(action, widgetId),
            Intent(context, PokedexWidgetProvider::class.java).apply {
                this.action = action
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                putExtra(PokedexWidgetProvider.EXTRA_ID, targetId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
