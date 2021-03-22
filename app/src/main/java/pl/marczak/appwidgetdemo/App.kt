package pl.marczak.appwidgetdemo

import android.app.Application
import androidx.work.Configuration
import androidx.work.impl.Scheduler

class App : Application(), Configuration.Provider {

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMaxSchedulerLimit(Scheduler.MAX_SCHEDULER_LIMIT)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
}
