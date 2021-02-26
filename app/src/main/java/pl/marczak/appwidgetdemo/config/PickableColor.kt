package pl.marczak.appwidgetdemo.config

import android.graphics.Color
import androidx.annotation.ColorInt

class PickableColor(
    val name: String,
    @ColorInt val color: Int,
    @ColorInt val textColor: Int = Color.BLACK
) {
    companion object {
        val NONE = PickableColor("Pick color", Color.WHITE)
    }
}