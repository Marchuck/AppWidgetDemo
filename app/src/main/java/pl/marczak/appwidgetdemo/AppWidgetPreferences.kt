package pl.marczak.appwidgetdemo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppWidgetPreferences(context: Context) {

    companion object {
        const val KEY_WIDGET_VIEWSTATE_ = "KEY_WIDGET_VIEWSTATE_"
    }

    private val gson = Gson()

    private val dataStore: DataStore<Preferences> =
        context.createDataStore(name = "app_widget")

    suspend fun saveWidget(state: WidgetViewState) {
        dataStore.edit { settings ->
            settings[keyOf(state.modelId)] = gson.toJson(state)
        }
    }

    fun getWidget(modelId: String): Flow<WidgetViewState?> {
        return dataStore.data.map {
            val json = it[keyOf(modelId)]
            try {
                gson.fromJson(json, WidgetViewState::class.java)
            } catch (error: Throwable) {
                null
            }
        }
    }

    private fun keyOf(modelId: String): Preferences.Key<String> {
        return stringPreferencesKey("$KEY_WIDGET_VIEWSTATE_$modelId")
    }
}
