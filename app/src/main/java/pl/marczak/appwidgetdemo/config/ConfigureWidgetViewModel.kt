package pl.marczak.appwidgetdemo.config

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.marczak.appwidgetdemo.AppWidgetPreferences
import pl.marczak.appwidgetdemo.MyWidgetProvider
import pl.marczak.appwidgetdemo.MyWidgetProvider.Companion.isValidAppWidgetId
import pl.marczak.appwidgetdemo.WidgetViewState


class ConfigureWidgetViewModel constructor(
    private val appWidgetPreferences: AppWidgetPreferences
) : ViewModel() {

    val viewState = MutableLiveData(
        ConfigViewState(
            widgetName = "",
            widgetId = MyWidgetProvider.NO_ID,
            backgroundColor = PickableColor.NONE,
            createButtonEnabled = false,
            isDone = false
        )
    )

    fun initialize(widgetId: Int) {
        val current = viewState.value ?: return
        val enableButton = current.shouldEnableButton(appWidgetId = widgetId)
        viewState.value = current.copy(
            widgetId = widgetId,
            createButtonEnabled = enableButton
        )
    }

    fun onColorPicked(newColor: PickableColor) {
        val current = viewState.value ?: return
        val enableButton = current.shouldEnableButton(color = newColor)
        viewState.value = current.copy(
            backgroundColor = newColor, createButtonEnabled = enableButton
        )
    }

    fun onNameChanged(newName: String) {
        val current = viewState.value ?: return
        val enableButton = current.shouldEnableButton(name = newName)
        viewState.value = current.copy(
            widgetName = newName,
            createButtonEnabled = enableButton
        )
    }

    private fun ConfigViewState.shouldEnableButton(
        name: String = widgetName,
        color: PickableColor = backgroundColor,
        appWidgetId: Int = widgetId
    ): Boolean {
        return name.length >= 3 && color != PickableColor.NONE && appWidgetId.isValidAppWidgetId
    }

    fun buildAppWidget() {
        viewModelScope.launch {
            runCatching {
                requireNotNull(viewState.value).apply {
                    val widget =
                        WidgetViewState(
                            widgetId,
                            widgetName,
                            backgroundColor.color,
                            isRunning = false,
                            isLoading = false
                        )
                    appWidgetPreferences.saveWidget(widget)
                }
            }.onFailure {
                viewState.value = viewState.value?.copy(isDone = false, error = it)
            }.onSuccess {
                viewState.value = viewState.value?.copy(isDone = true)
            }
        }
    }
}