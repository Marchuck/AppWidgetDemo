package pl.marczak.appwidgetdemo

import androidx.annotation.ColorInt
import java.io.Serializable

data class WidgetViewState(
    val widgetId: Int,
    val modelId: String,
    val name: String,
    @ColorInt val backgroundColor: Int,
    val isRunning: Boolean,
    val isLoading: Boolean,
) : Serializable
