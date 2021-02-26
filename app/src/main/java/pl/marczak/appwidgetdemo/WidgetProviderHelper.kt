package pl.marczak.appwidgetdemo

import android.content.Context
import android.widget.Toast
import pl.marczak.appwidgetdemo.MyWidgetProvider.Companion.appWidgetManager


fun Context.debug(s: Any) {
    Toast.makeText(applicationContext, s.toString(), Toast.LENGTH_SHORT).show()
}

fun Context.error(throwable: Throwable, s: Any = "") {
    debug("Error($throwable): $s")
}

fun Context.updateWidget(
    id: Int,
    preferences: AppWidgetPreferences,
    renderer: MyWidgetRenderer = MyWidgetRenderer(this)
) {
    preferences.getWidget(id)?.let { updateWidget(it, renderer) }
}

fun Context.updateWidget(viewState: WidgetViewState, renderer: MyWidgetRenderer) {
    appWidgetManager.updateAppWidget(viewState.widgetId, renderer.render(viewState))
}

fun Context.updateWidgets(
    appWidgetIds: IntArray
) {
    val context = this
    val widgetPreferences = AppWidgetPreferences(context.applicationContext)
    val renderer = MyWidgetRenderer(context)

    appWidgetIds.onEach { id ->
        updateWidget(id, widgetPreferences, renderer)
    }
}
