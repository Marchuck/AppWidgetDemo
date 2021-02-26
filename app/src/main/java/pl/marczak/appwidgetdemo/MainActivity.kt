package pl.marczak.appwidgetdemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.marczak.appwidgetdemo.MyWidgetProvider.Companion.appWidgetManager
import pl.marczak.appwidgetdemo.background.UpdateWidgetsWorker
import pl.marczak.appwidgetdemo.databinding.ActivityMainBinding
import pl.marczak.appwidgetdemo.databinding.AppwidgetStopwatchBinding

class MainActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private val adapter = StopwatchesAdapter()
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        this.binding = binding
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter
        val preferences = AppWidgetPreferences(this)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(
                this,
                MyWidgetProvider::class.java
            )
        )
        val items = preferences.widgets(ids)
        adapter.submitList(items)
        binding.widgetsEmpty.isVisible = items.isEmpty()

        UpdateWidgetsWorker.startPeriodically(this)
    }
}

val diff = object : DiffUtil.ItemCallback<WidgetViewState>() {
    override fun areItemsTheSame(oldItem: WidgetViewState, newItem: WidgetViewState): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: WidgetViewState, newItem: WidgetViewState): Boolean {
        return oldItem == newItem
    }
}

class StopwatchesAdapter : ListAdapter<WidgetViewState, WidgetViewHolder>(diff) {

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
    fun bind(state: WidgetViewState) = MyWidgetRenderer.bind(binding, state)
}
