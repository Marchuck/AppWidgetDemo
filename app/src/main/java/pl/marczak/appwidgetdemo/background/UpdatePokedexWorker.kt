package pl.marczak.appwidgetdemo.background

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import androidx.work.*
import coil.Coil
import coil.api.get
import com.google.gson.Gson
import pl.marczak.appwidgetdemo.pokesample.PokeClient
import pl.marczak.appwidgetdemo.pokesample.PokedexRenderer
import pl.marczak.appwidgetdemo.pokesample.PokedexViewState
import pl.marczak.appwidgetdemo.pokesample.PokedexWidgetProvider
import pl.marczak.appwidgetdemo.workManager

class UpdatePokedexWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pokemonId = workerParams.inputData.getInt("POKEMON_ID", -1)
        val widgetId = workerParams.inputData.getInt("WIDGET_ID", -1)
        if (pokemonId != -1 && widgetId != -1) {

            val renderer = PokedexRenderer(applicationContext)
            val client = PokeClient(Gson())
            val viewState =
                providePokeViewState(
                    client,
                    pokemonId,
                    widgetId
                ) ?: return Result.failure()

            val remoteViews = renderer.render(viewState)

            val intent = Intent(context, PokedexWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(PokedexWidgetProvider.EXTRA_ID, pokemonId)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                putExtra(PokedexWidgetProvider.EXTRA_REMOTE_VIEWS, remoteViews)
            }
            context.sendBroadcast(intent)
            return Result.success()
        } else {
            return Result.failure()
        }
    }

    companion object {

        @JvmStatic
        suspend fun providePokeViewState(
            pokeClient: PokeClient,
            pokemonId: Int,
            widgetId: Int
        ): PokedexViewState.Poke? {
            return try {
                val pokeResponse = pokeClient.getPokemon(pokemonId)
                val drawable = Coil.loader().get(uri = pokeResponse.sprites.front_default) as? BitmapDrawable

                PokedexViewState.Poke(
                    pokemonId,
                    widgetId,
                    pokeResponse.name,
                    pokeResponse.types.joinToString { it.type.name },
                    drawable
                )
            } catch (t: Throwable) {
                null
            }
        }

        private const val TAG_APPWIDGET_UPDATE = "TAG_APPWIDGET_UPDATE"

        @JvmStatic
        fun enqueue(context: Context, id: Int, widgetId: Int) {
            val request = OneTimeWorkRequestBuilder<UpdatePokedexWorker>()
                .setInputData(
                    workDataOf(
                        "POKEMON_ID" to id,
                        "WIDGET_ID" to widgetId
                    )
                )
                .build()

            context.workManager.enqueueUniqueWork(
                TAG_APPWIDGET_UPDATE,
                ExistingWorkPolicy.APPEND,
                request
            )
        }
    }
}
