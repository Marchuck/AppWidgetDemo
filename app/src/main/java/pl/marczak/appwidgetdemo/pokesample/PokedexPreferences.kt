package pl.marczak.appwidgetdemo.pokesample

import android.content.Context
import androidx.core.content.edit

class PokedexPreferences(context: Context) {

    private val sharedPreferences = context
        .applicationContext
        .getSharedPreferences("pokedex", Context.MODE_PRIVATE)


    fun store(widgetId: Int, id: Int) {
        sharedPreferences.edit(commit = true) { putInt(widgetId.toString(), id) }
    }

    fun retrieve(widgetId: Int): Int {
        return sharedPreferences.getInt(widgetId.toString(), widgetId)
    }

    fun delete(appWidgetIds: IntArray) {
        val editor = sharedPreferences.edit()
        for (key in appWidgetIds) {
            editor.remove(key.toString())
        }
        editor.commit()
    }
}
