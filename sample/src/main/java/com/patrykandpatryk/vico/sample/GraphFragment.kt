package com.patrykandpatryk.vico.sample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.patrykandpatryk.vico.R
import com.patrykandpatryk.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatryk.vico.core.entry.ChartEntry
import com.patrykandpatryk.vico.core.entry.ChartEntryModel
import com.patrykandpatryk.vico.core.entry.FloatEntry
import com.patrykandpatryk.vico.core.entry.entryModelOf
import com.patrykandpatryk.vico.core.extension.setFieldValue
import com.patrykandpatryk.vico.view.chart.ChartView

public class GraphFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart(view)
    }

    private fun setupChart(view: View) {
        val chart = view.findViewById<ChartView>(R.id.chart)
        chart.post {
            chart.chart?.axisValuesOverrider = AxisValuesOverrider.fixed(minY = 400f, maxY = 1100f)
            chart.setModel(entryModelOf(getDataPoints()))
        }
    }

    private fun getDataPoints(): MutableList<FloatEntry> {
        val data = mutableListOf<FloatEntry>()
        for (i in 0..1000) {
            data.add(FloatEntry(i.toFloat(), (600..700).random().toFloat()))
        }
        return data
    }
}
