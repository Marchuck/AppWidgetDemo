package pl.marczak.appwidgetdemo.goasyncsample

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson

class MyWidgetPreferences(context: Context) {

    companion object {
        const val KEY_WIDGET_VIEWSTATE_ = "KEY_WIDGET_VIEWSTATE_"
    }

    private val gson = Gson()

    private val sharedPreferences = context
        .applicationContext
        .getSharedPreferences("app_widgets", Context.MODE_PRIVATE)

    @SuppressLint("ApplySharedPref")
    fun saveWidget(stateMy: MyWidgetViewState) {
        sharedPreferences.edit(commit = true) {
            putString(keyNameOf(stateMy.widgetId), gson.toJson(stateMy))
        }
    }

    fun getWidget(widgetId: Int): MyWidgetViewState? {
        val json = sharedPreferences.getString(keyNameOf(widgetId), null) ?: return null
        return try {
            gson.fromJson(json, MyWidgetViewState::class.java)
        } catch (error: Throwable) {
            null
        }
    }

    fun removeWidget(id: Int) {
        sharedPreferences.edit(commit = true) {
            remove(keyNameOf(id))
        }
    }

    fun widgets(ids: IntArray): List<MyWidgetViewState> {
        return ids.toList().mapNotNull { getWidget(it) }
    }

    private fun keyNameOf(widgetId: Int): String {
        return "$KEY_WIDGET_VIEWSTATE_$widgetId"
    }
}
