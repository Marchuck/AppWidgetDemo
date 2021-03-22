package pl.marczak.appwidgetdemo.pokesample.config

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import pl.marczak.appwidgetdemo.appWidgetId
import pl.marczak.appwidgetdemo.appWidgetManager
import pl.marczak.appwidgetdemo.background.UpdatePokedexWorker
import pl.marczak.appwidgetdemo.databinding.ActivityPokedexBinding
import pl.marczak.appwidgetdemo.isValidAppWidgetId
import pl.marczak.appwidgetdemo.pokesample.*
import pl.marczak.appwidgetdemo.pokesample.PokedexWidgetProvider.Companion.MAX_POKE_ID
import pl.marczak.appwidgetdemo.pokesample.PokedexWidgetProvider.Companion.MIN_POKE_ID

class ConfigurePokedexActivity : AppCompatActivity() {

    lateinit var binding: ActivityPokedexBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokedexBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val widgetId = intent.extras.appWidgetId
        if (!widgetId.isValidAppWidgetId) {
            finish()
        } else {

            binding.pokeIdInput.addTextChangedListener {
                val integerCandidate: Int? = it.toString().toIntOrNull()
                binding.button.isEnabled = integerCandidate != null
            }
            binding.button.setOnClickListener {
                val value =
                    binding.pokeIdInput.text.toString().toIntOrNull() ?: return@setOnClickListener
                if (value !in MIN_POKE_ID..MAX_POKE_ID) {
                    Toast.makeText(
                        this,
                        "Please enter value from range [$MIN_POKE_ID..$MAX_POKE_ID]",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    savePoke(widgetId, value)
                }
            }
        }
    }

    private fun savePoke(widgetId: Int, pokemonId: Int) {
        lifecycleScope.launch {
            val renderer = PokedexRenderer(applicationContext)
            val client = PokeClient(Gson())
            val viewState = UpdatePokedexWorker.providePokeViewState(client, pokemonId, widgetId)
                ?: PokedexViewState.Error(pokemonId, widgetId)
            PokedexPreferences(applicationContext).store(widgetId, pokemonId)
            appWidgetManager.updateAppWidget(widgetId, renderer.render(viewState))
            exitWithResult(widgetId)
        }
    }

    private fun exitWithResult(widgetId: Int) {
        val resultValue = PokedexWidgetProvider.newWidget(widgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}
