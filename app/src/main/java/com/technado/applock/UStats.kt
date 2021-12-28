package com.technado.applock

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.*

object UStats {
    val TAG = UStats::class.java.simpleName

    @SuppressLint("WrongConstant")
    fun getUsageStatsList(context: Context): List<UsageStats> {
        val usm = context.getSystemService("usagestats") as UsageStatsManager
        val calendar: Calendar = Calendar.getInstance()
        val endTime: Long = calendar.getTimeInMillis()
        calendar.add(Calendar.DATE, -1)
        val startTime: Long = calendar.getTimeInMillis()
        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
    }
}