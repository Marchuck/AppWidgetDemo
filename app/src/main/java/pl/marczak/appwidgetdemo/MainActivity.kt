package pl.marczak.appwidgetdemo

import android.app.PendingIntent
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_PREVIEW
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import pl.marczak.appwidgetdemo.background.UpdatePokedexWorker.Companion.providePokeViewState
import pl.marczak.appwidgetdemo.background.UpdateWidgetsWorker
import pl.marczak.appwidgetdemo.databinding.ActivityMainBinding
import pl.marczak.appwidgetdemo.pokesample.PokeClient
import pl.marczak.appwidgetdemo.pokesample.PokedexRenderer
import pl.marczak.appwidgetdemo.pokesample.PokedexWidgetProvider

class MainActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private var binding: ActivityMainBinding? = null

    private val pokeClient by lazy { PokeClient(Gson()) }
    private val pokedexRenderer by lazy { PokedexRenderer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        this.binding = binding
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.pinWidget.visibility = View.VISIBLE
            binding.pinWidget.setOnClickListener { pinAppWidget() }
        } else {
            binding.pinWidget.visibility = View.GONE
        }

        binding.switchPeriodicUpdate.setOnCheckedChangeListener { buttonView, isChecked ->
            togglePeriodicUpdates(
                isChecked
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pinAppWidget() = lifecycleScope.launchWhenResumed {
        val pokemonId = integerInput() ?: return@launchWhenResumed

        val widgetProvider = ComponentName(applicationContext, PokedexWidgetProvider::class.java)

        val extras = bundleOf(EXTRA_APPWIDGET_PREVIEW to imagePreviewOf(pokemonId))
        val pendingIntent = initialBroadcastOf(pokemonId = pokemonId)
        appWidgetManager.requestPinAppWidget(widgetProvider, extras, pendingIntent)
    }

    private fun initialBroadcastOf(pokemonId: Int): PendingIntent {
        return PendingIntent.getBroadcast(
            applicationContext,
            0,
            Intent(applicationContext, PokedexWidgetProvider::class.java).apply {
                action = PokedexWidgetProvider.ACTION_CURRENT
                putExtra(PokedexWidgetProvider.EXTRA_ID, pokemonId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private suspend fun imagePreviewOf(pokemonId: Int): RemoteViews? {
        return providePokeViewState(pokeClient, pokemonId, pokemonId)
            ?.let(pokedexRenderer::render)
    }

    private suspend fun integerInput() = suspendCancellableCoroutine<Int?> { c ->
        val editText = EditText(this).apply {
            this.inputType = EditorInfo.TYPE_CLASS_NUMBER
            this.hint = "Enter poke #id"
            this.setText("1")
            this.setSelection(length())
        }
        MaterialAlertDialogBuilder(this)
            .setView(editText)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                val string = editText.text?.toString()
                val integer = string?.toIntOrNull()
                c.resumeWith(Result.success(integer))
                dialog.dismiss()
            }.setOnCancelListener { c.cancel() }.show()

    }

    private fun togglePeriodicUpdates(checked: Boolean) {
        if (checked) {
            UpdateWidgetsWorker.startPeriodically(this)
        } else {
            UpdateWidgetsWorker.stopPeriodicUpdates(this)
        }
    }
}
