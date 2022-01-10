package com.technado.applock

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.technado.applock.UStats.getUsageStatsList

class MainActivity : AppCompatActivity() {
    var progRessDialog: ProgressDialog? = null
    private lateinit var installedAppsList: ArrayList<AppModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        installedAppsList = ArrayList()
        progRessDialog = ProgressDialog(this)
        progRessDialog!!.setMessage("Loading...")
        progRessDialog!!.show()

        startServiceToGetRecentApps()

        Handler(Looper.getMainLooper()).postDelayed({
            getInstalledApps()
            progRessDialog!!.dismiss()
            recyclerView?.adapter =
                AppsLockAdapter(
                    this,
                    getInstalledApps()
                )
        }, 500)
    }

    fun startServiceToGetRecentApps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getUsageStatsList(this).isEmpty()) {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }
        startService(Intent(baseContext, AppLockService::class.java))
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getInstalledApps(): ArrayList<AppModel> {
        val packs = this.packageManager?.getInstalledPackages(0)
        for (i in packs?.indices!!) {
            val p = packs[i]
            if (!isSystemPackage(p)) {
                val appName = p.applicationInfo.loadLabel(this.packageManager!!).toString()
                val icon = p.applicationInfo.loadIcon(this.packageManager!!)
                val packages = p.applicationInfo.packageName
                Log.d("packages", "getInstalledApps: " + packages)
                installedAppsList.add(AppModel(appName, icon, packages))
            }
        }
        return installedAppsList
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }
}