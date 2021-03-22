package pl.marczak.appwidgetdemo.goasyncsample

import androidx.annotation.ColorInt
import java.io.Serializable

data class MyWidgetViewState(
    val widgetId: Int,
    val name: String,
    @ColorInt val backgroundColor: Int,
    val isRunning: Boolean,
    val isLoading: Boolean,
    val toggleDate: Long = 0L
) : Serializable
