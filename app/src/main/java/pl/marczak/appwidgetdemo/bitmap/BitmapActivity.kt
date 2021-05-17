package pl.marczak.appwidgetdemo.bitmap

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

class BitmapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val widgetId = intent.extras.appWidgetId
        val resultValue = CustomBitmapProvider.newWidget(widgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}
