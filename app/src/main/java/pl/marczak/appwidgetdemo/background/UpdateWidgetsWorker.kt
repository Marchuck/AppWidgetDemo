package pl.marczak.appwidgetdemo.background

import android.content.Context
import androidx.work.*
import pl.marczak.appwidgetdemo.goasyncsample.MyWidgetProvider
import pl.marczak.appwidgetdemo.workManager
import java.util.concurrent.TimeUnit

class UpdateWidgetsWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        MyWidgetProvider.updateAllWidgets(applicationContext)
        return Result.success()
    }

    companion object {

        private const val TAG_PERIODIC_APPWIDGETS_UPDATE = "TAG_PERIODIC_APPWIDGETS_UPDATE"

        fun startPeriodically(context: Context) {
            val request = PeriodicWorkRequestBuilder<UpdateWidgetsWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()

            context.workManager.enqueueUniquePeriodicWork(
                TAG_PERIODIC_APPWIDGETS_UPDATE,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }

        fun stopPeriodicUpdates(context: Context) {
            context.workManager.cancelUniqueWork(TAG_PERIODIC_APPWIDGETS_UPDATE)
        }
    }
}
