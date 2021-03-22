package pl.marczak.appwidgetdemo.goasyncsample.config

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import pl.marczak.appwidgetdemo.*
import pl.marczak.appwidgetdemo.databinding.ActivityConfigureWidgetBinding
import pl.marczak.appwidgetdemo.goasyncsample.MyWidgetPreferences
import pl.marczak.appwidgetdemo.goasyncsample.MyWidgetProvider
import pl.marczak.appwidgetdemo.goasyncsample.updateWidget

class MyWidgetActivity : AppCompatActivity() {

    lateinit var binding: ActivityConfigureWidgetBinding

    lateinit var viewModel: ConfigureWidgetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigureWidgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val widgetId = intent.extras.appWidgetId
        if (!widgetId.isValidAppWidgetId) {
            finish()
        } else {
            viewModel = ConfigureWidgetViewModel(
                MyWidgetPreferences(applicationContext)
            )
            configureAppWidget(widgetId)
        }
    }

    private fun configureAppWidget(widgetId: Int) {
        binding.name.doAfterTextChanged {
            val newText = it?.toString() ?: ""
            viewModel.onNameChanged(newText)
        }
        binding.colorPicker.setOnClickListener {
            showColorPicker(it) { color ->
                viewModel.onColorPicked(color)
            }
        }
        binding.buttonCreate.setOnClickListener {
            viewModel.buildAppWidget()
        }

        viewModel.initialize(widgetId)

        val lifecycleOwner: LifecycleOwner = this

        viewModel.viewState.observe(lifecycleOwner) { state ->
            binding.colorPicker.setTextColor(state.backgroundColor.textColor)
            binding.colorPicker.setBackgroundColor(state.backgroundColor.color)
            binding.colorPicker.setText(state.backgroundColor.name)
            binding.buttonCreate.isEnabled = state.createButtonEnabled

            if (state.isDone) {
                finishWithResult(state.widgetId)
            }
        }
    }

    private fun showColorPicker(anchor: View, onPicked: (PickableColor) -> Unit) {
        val popupMenu = PopupMenu(anchor.context, anchor)
        val colors = listOf(
            PickableColor("GREEN", Color.GREEN),
            PickableColor("BLUE", Color.BLUE, Color.WHITE),
            PickableColor("RED", Color.RED, Color.WHITE),
            PickableColor("YELLOW", Color.YELLOW),
            PickableColor("MAGENTA", Color.MAGENTA, Color.WHITE),
            PickableColor("CYAN", Color.CYAN)
        )
        for (color in colors) {
            popupMenu.menu.add(color.name)
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val pickedColorName = menuItem.title.toString()
            colors.firstOrNull { color ->
                color.name == pickedColorName
            }?.let { onPicked(it) }
            true
        }
        popupMenu.show()
    }

    private fun finishWithResult(widgetId: Int) {
        applicationContext.updateWidget(widgetId)

        val resultValue = MyWidgetProvider.newWidget(widgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}
