package pl.marczak.appwidgetdemo.background

import android.content.Context
import androidx.work.*
import pl.marczak.appwidgetdemo.MyWidgetProvider
import java.util.concurrent.TimeUnit

class UpdateWidgetsWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        MyWidgetProvider.updateAllWidgets(applicationContext)
        return Result.success()
    }

    companion object {

        private const val TAG_PERIODIC_APPWIDGETS_UPDATE = "TAG_PERIODIC_APPWIDGETS_UPDATE"

        @JvmStatic
        fun startPeriodically(context: Context) {
            val request = PeriodicWorkRequestBuilder<UpdateWidgetsWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()

            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(
                TAG_PERIODIC_APPWIDGETS_UPDATE,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
