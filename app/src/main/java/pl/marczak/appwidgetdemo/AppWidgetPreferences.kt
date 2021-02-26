package pl.marczak.appwidgetdemo

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson

class AppWidgetPreferences(context: Context) {

    companion object {
        const val KEY_WIDGET_VIEWSTATE_ = "KEY_WIDGET_VIEWSTATE_"
    }

    private val gson = Gson()

    private val sharedPreferences = context
        .applicationContext
        .getSharedPreferences("app_widgets", Context.MODE_PRIVATE)

    @SuppressLint("ApplySharedPref")
    fun saveWidget(state: WidgetViewState) {
        sharedPreferences.edit(commit = true) {
            putString(keyNameOf(state.widgetId), gson.toJson(state))
        }
    }

    fun getWidget(widgetId: Int): WidgetViewState? {
        val json = sharedPreferences.getString(keyNameOf(widgetId), null) ?: return null
        return try {
            gson.fromJson(json, WidgetViewState::class.java)
        } catch (error: Throwable) {
            null
        }
    }

    fun removeWidgets(ids: IntArray) {
        sharedPreferences.edit(commit = true) {
            ids.forEach {
                remove(keyNameOf(it))
            }
        }
    }

    fun widgets(ids: IntArray): List<WidgetViewState> {
        return ids.toList().mapNotNull { getWidget(it) }
    }

    private fun keyNameOf(widgetId: Int): String {
        return "$KEY_WIDGET_VIEWSTATE_$widgetId"
    }
}
