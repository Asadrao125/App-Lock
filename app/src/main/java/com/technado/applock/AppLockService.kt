package com.technado.applock

import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*

class AppLockService : Service() {
    lateinit var sharedPref: SharedPref

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sharedPref = SharedPref
        sharedPref.init(this)
        val closeActivity = Thread {
            for (i in 0..99) {
                Thread.sleep(1000)
                intentFunction()
            }
        }
        closeActivity.start()
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun intentFunction() {
        val topPackageName: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mUsageStatsManager =
                getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            // We get usage stats for the last 10 seconds
            val stats = mUsageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                time - 1000 * 10,
                time
            )
            // Sort the stats by the last time used
            if (stats != null) {
                val mySortedMap: SortedMap<Long, UsageStats> =
                    TreeMap<Long, UsageStats>()
                for (usageStats in stats) {
                    mySortedMap.put(usageStats.lastTimeUsed, usageStats)
                }
                if (!mySortedMap.isEmpty()) {
                    topPackageName =
                        mySortedMap.get(mySortedMap.lastKey())!!.getPackageName()

                    Log.d("tagtag", "onStartCommand: " + topPackageName)

                    if (topPackageName.equals(sharedPref.read("pkg-name", ""))) {

                        Log.d("tagtag", "onStartCommand: Matched")

                        val dialogIntent = Intent(this, DummyActivity::class.java)
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(dialogIntent)

                    } else {
                        Log.d("tagtag", "onStartCommand: Not Matching")
                    }
                }
            }
        }
    }
}