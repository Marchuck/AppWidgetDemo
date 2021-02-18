package pl.marczak.appwidgetdemo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pl.marczak.appwidgetdemo.WidgetProvider.Companion.appWidgetId
import pl.marczak.appwidgetdemo.WidgetProvider.Companion.isValidAppWidgetId

class ConfigureWidgetActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure_widget)

        val widgetId = intent.extras.appWidgetId

        if (!widgetId.isValidAppWidgetId) {
            finish()
        } else {
            configureAppWidget(widgetId)
        }
    }

    private fun configureAppWidget(widgetId: Int) {

    }
}
