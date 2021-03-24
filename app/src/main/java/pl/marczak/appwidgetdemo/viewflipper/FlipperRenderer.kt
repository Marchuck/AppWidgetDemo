package pl.marczak.appwidgetdemo.viewflipper

import android.content.Context
import android.widget.RemoteViews
import pl.marczak.appwidgetdemo.R

class FlipperRenderer(
    private val context: Context
) {

    fun render(sth: String): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_flipper)
        remoteViews.setTextViewText(R.id.name, sth)
        return remoteViews
    }
}
