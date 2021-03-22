package pl.marczak.appwidgetdemo.goasyncsample.config

data class ConfigViewState(
    val widgetName: String,
    val widgetId: Int,
    val backgroundColor: PickableColor,
    val createButtonEnabled: Boolean,
    val isDone: Boolean,
    val error: Throwable? = null
)
