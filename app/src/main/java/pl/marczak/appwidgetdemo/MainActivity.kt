package pl.marczak.appwidgetdemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.marczak.appwidgetdemo.background.UpdateWidgetsWorker
import pl.marczak.appwidgetdemo.databinding.ActivityMainBinding
import pl.marczak.appwidgetdemo.databinding.AppwidgetStopwatchBinding
import pl.marczak.appwidgetdemo.goasyncsample.MyWidgetPreferences
import pl.marczak.appwidgetdemo.goasyncsample.MyWidgetProvider
import pl.marczak.appwidgetdemo.goasyncsample.MyWidgetRenderer
import pl.marczak.appwidgetdemo.goasyncsample.MyWidgetViewState

class MainActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private val preferences by lazy { MyWidgetPreferences(this) }
    private val adapter = StopwatchesAdapter()
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        this.binding = binding
        setContentView(binding.root)
        binding.recyclerView.adapter = adapter


        binding.switchPeriodicUpdate.setOnCheckedChangeListener { buttonView, isChecked ->
            togglePeriodicUpdates(
                isChecked
            )
        }
    }

    private fun togglePeriodicUpdates(checked: Boolean) {
        if (checked) {
            UpdateWidgetsWorker.startPeriodically(this)
        } else {
            UpdateWidgetsWorker.stopPeriodicUpdates(this)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(
                this,
                MyWidgetProvider::class.java
            )
        )
        val items = preferences.widgets(ids)
        adapter.submitList(items)
        binding?.widgetsEmpty?.isVisible = items.isEmpty()
    }
}

fun provideItemCallback() = object : DiffUtil.ItemCallback<MyWidgetViewState>() {
    override fun areItemsTheSame(oldItem: MyWidgetViewState, newItem: MyWidgetViewState): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: MyWidgetViewState,
        newItem: MyWidgetViewState
    ): Boolean {
        return oldItem == newItem
    }
}

class StopwatchesAdapter : ListAdapter<MyWidgetViewState, WidgetViewHolder>(provideItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AppwidgetStopwatchBinding.inflate(inflater, parent, false)
        return WidgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class WidgetViewHolder(
    private val binding: AppwidgetStopwatchBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(stateMy: MyWidgetViewState) = MyWidgetRenderer.bind(binding, stateMy)
}
