package pl.marczak.appwidgetdemo.pokesample

import android.graphics.drawable.BitmapDrawable

sealed class PokedexViewState(open val id: Int, open val widgetId: Int) {

    data class Loading(
        override val id: Int,
        override val widgetId: Int
    ) : PokedexViewState(id, widgetId)

    data class Poke(
        override val id: Int,
        override val widgetId: Int,
        val name: String,
        val description: String,
        val image: BitmapDrawable?
    ) : PokedexViewState(id, widgetId)

    data class Error(
        override val id: Int,
        override val widgetId: Int
    ) : PokedexViewState(id, widgetId)
}
