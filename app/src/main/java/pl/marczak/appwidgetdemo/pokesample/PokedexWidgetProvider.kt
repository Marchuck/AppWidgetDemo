package pl.marczak.appwidgetdemo.pokesample

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import pl.marczak.appwidgetdemo.appWidgetId
import pl.marczak.appwidgetdemo.appWidgetManager
import pl.marczak.appwidgetdemo.background.UpdatePokedexWorker
import pl.marczak.appwidgetdemo.isValidAppWidgetId


class PokedexWidgetProvider : AppWidgetProvider() {

    companion object {

        val EXTRA_ID: String = "EXTRA_ID"
        val EXTRA_REMOTE_VIEWS: String = "EXTRA_REMOTE_VIEWS"
        val ACTION_NEXT = "ACTION_NEXT"
        val ACTION_PREV = "ACTION_PREV"
        val ACTION_CURRENT = "ACTION_CURRENT"
        val MIN_POKE_ID = 1
        val MAX_POKE_ID = 898

        fun newWidget(widgetId: Int) = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
    }

    private var preferences: PokedexPreferences? = null
    private var renderer: PokedexRenderer? = null

    override fun onReceive(context: Context, intent: Intent) {
        val pokemonId = intent.getIntExtra(EXTRA_ID, -1)
        val widgetId = intent.extras.appWidgetId

        preferences = PokedexPreferences(context)
        renderer = PokedexRenderer(context)

        when (intent.action) {
            ACTION_NEXT, ACTION_PREV, ACTION_CURRENT -> {
                if (widgetId.isValidAppWidgetId) {
                    val targetPokemonId = when (intent.action) {
                        ACTION_NEXT -> (pokemonId + 1).coerceAtMost(MAX_POKE_ID)
                        ACTION_PREV -> (pokemonId - 1).coerceAtLeast(MIN_POKE_ID)
                        else -> pokemonId
                    }
                    preferences?.store(widgetId, targetPokemonId)
                    onUpdate(context, context.appWidgetManager, intArrayOf(widgetId))
                }
            }
            else -> {
                if (intent.hasExtra(EXTRA_REMOTE_VIEWS)) {
                    val views = intent.getParcelableExtra<RemoteViews>(EXTRA_REMOTE_VIEWS)
                    context.appWidgetManager.updateAppWidget(widgetId, views)
                } else {
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
        val pokemonIds = mutableListOf<Int>()
        for (widgetId in appWidgetIds) {
            val pokemonId = preferences?.retrieve(widgetId) ?: continue
            pokemonIds.add(pokemonId)
            val remoteViews = renderer?.render(
                PokedexViewState.Loading(
                    pokemonId,
                    widgetId
                )
            )
            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
        UpdatePokedexWorker.enqueue(
            context,
            appWidgetIds,
            pokemonIds.toIntArray()
        )
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        preferences?.delete(appWidgetIds)
    }
}
