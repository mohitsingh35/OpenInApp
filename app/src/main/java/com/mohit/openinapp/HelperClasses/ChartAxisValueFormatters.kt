package com.mohit.openinapp.HelperClasses

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class XAxisValueFormatter(private val time: List<Float>) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val startIndex = 3
        val position = value.toInt()

        return if (position >= startIndex && position < time.size) {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, time[position].toInt())
            calendar.set(Calendar.MINUTE, 0)
            sdf.format(calendar.time)
        } else {
            ""
        }
    }
}



class YAxisValueFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return value.toInt().toString()
    }
}
