package pl.marczak.appwidgetdemo.bitmap

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.widget.RemoteViews
import pl.marczak.appwidgetdemo.R


class CustomBitmapProvider : AppWidgetProvider() {
    companion object {
        fun newWidget(widgetId: Int) = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (widgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(widgetId, context.generateImage(widgetId))
        }
    }

    private fun Context.generateImage(id: Int): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.appwidget_bitmap)
        remoteViews.setTextViewText(R.id.name, "AppWidget $id")
        val bitmap = Bitmap.createBitmap(
            px(80f).toInt(),
            px(80f).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val paintFill = Paint().apply {
            isAntiAlias = true
            shader = LinearGradient(
                0f, 0f, 0f, canvas.height.toFloat(),
                intArrayOf(Color.RED, Color.BLUE),
                null,
                Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
        }
        val data = (0..80).map { it / 100f }
        val pricePath = Path().apply {

            fillType = Path.FillType.EVEN_ODD

            // -100f to not see a line going from top left corner down to start position
            moveTo(-100f, (0f) * canvas.height)

            data.forEachIndexed { index, it ->
                lineTo(
                    (canvas.width * index).toFloat() / (data.size - 1.0f),
                    (1 - it) * canvas.height
                )
            }

            // Make line around and outside of screen to not see line going from end to start
            lineTo(1.1f * canvas.width.toFloat(), canvas.height * 1.5.toFloat())

            lineTo(-1.1f * canvas.width.toFloat(), canvas.height * 1.5.toFloat())

            close()
        }
        canvas.drawPath(pricePath, paintFill)

        remoteViews.setImageViewBitmap(R.id.imageView, bitmap)
        return remoteViews
    }


    fun Context.px(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}

