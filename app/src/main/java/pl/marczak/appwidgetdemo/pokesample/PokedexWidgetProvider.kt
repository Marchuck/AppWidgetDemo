package pl.marczak.appwidgetdemo.pokesample

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import pl.marczak.appwidgetdemo.appWidgetId
import pl.marczak.appwidgetdemo.appWidgetManager
import pl.marczak.appwidgetdemo.background.UpdatePokedexWorker
import pl.marczak.appwidgetdemo.debug
import pl.marczak.appwidgetdemo.isValidAppWidgetId


class PokedexWidgetProvider : AppWidgetProvider() {

    companion object {

        val EXTRA_ID: String = "EXTRA_ID"
        val EXTRA_REMOTE_VIEWS: String = "EXTRA_REMOTE_VIEWS"
        val ACTION_NEXT = "ACTION_NEXT"
        val ACTION_PREV = "ACTION_PREV"
        val MIN_POKE_ID = 1
        val MAX_POKE_ID = 898

        fun newWidget(widgetId: Int) = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pokemonId = intent.getIntExtra(EXTRA_ID, 1)
        val widgetId = intent.extras.appWidgetId

        when (intent.action) {
            ACTION_NEXT, ACTION_PREV -> {
                if (widgetId.isValidAppWidgetId) {
                    val targetPokemonId = when (intent.action) {
                        ACTION_NEXT -> (pokemonId + 1).coerceAtMost(MAX_POKE_ID)
                        ACTION_PREV -> (pokemonId - 1).coerceAtLeast(MIN_POKE_ID)
                        else -> pokemonId
                    }

                    PokedexPreferences(context).store(widgetId, targetPokemonId)
                    context.appWidgetManager.updateAppWidget(
                        widgetId,
                        PokedexRenderer(context).render(
                            PokedexViewState.Loading(
                                targetPokemonId,
                                widgetId
                            )
                        )
                    )
                    UpdatePokedexWorker.enqueue(context, targetPokemonId, widgetId)
                }
            }
            else -> {
                if (intent.hasExtra(EXTRA_REMOTE_VIEWS)) {
                    val views = intent.getParcelableExtra<RemoteViews>(EXTRA_REMOTE_VIEWS)
                    context.appWidgetManager.updateAppWidget(widgetId, views)
                }else{
                    super.onReceive(context, intent)
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val prefs = PokedexPreferences(context)
        for (id in appWidgetIds) {
            UpdatePokedexWorker.enqueue(context, prefs.retrieve(id), id)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        PokedexPreferences(context).delete(appWidgetIds)
    }
}
