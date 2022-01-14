package com.technado.applock

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.recyclerview.widget.RecyclerView

class AppsLockAdapter(var context: Context, var list: List<AppModel>) :
    RecyclerView.Adapter<AppsLockAdapter.MyViewHolder>() {
    lateinit var sharedPref: SharedPref

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_apps, parent, false)
        sharedPref = SharedPref
        sharedPref.init(context)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.appName.text = list.get(position).getName()
        holder.image.setImageDrawable(list.get(position).getIcon())

        holder.itemView.setOnClickListener(View.OnClickListener {
            holder.image.setImageDrawable(context.resources.getDrawable(R.drawable.ic_lock))
            val intent =
                context.packageManager.getLaunchIntentForPackage(list.get(position).getPackages())

            context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appName: TextView
        var image: ImageView

        init {
            appName = itemView.findViewById(R.id.appName)
            image = itemView.findViewById(R.id.image)
        }
    }

    fun addShortcutToHomeScreen(context: Context?, list: List<AppModel>, position: Int) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context!!)) {
            val addIntent = Intent()
            addIntent.putExtra("duplicate", false)
            addIntent.setAction(Intent.ACTION_MAIN)
            addIntent.setComponent(
                ComponentName(
                    list.get(position).getPackages(),
                    list.get(position).getPackages()
                )
            )
            val shortcutInfo = ShortcutInfoCompat.Builder(context, list.get(position).getPackages())
                .setIntent(
                    addIntent
                ) // !!! intent's action must be set on oreo
                .setShortLabel(list.get(position).getName())
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_app))
                .build()
            ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null)
        } else {
            Toast.makeText(context, "ELSE " + sharedPref.read("pkg-name", ""), Toast.LENGTH_SHORT)
                .show()
        }
    }

    @SuppressLint("NewApi")
    fun createWebActivityShortcut(position: Int) {
        val shortcutManager = ContextCompat.getSystemService<ShortcutManager>(
            context,
            ShortcutManager::class.java
        )
        if (shortcutManager!!.isRequestPinShortcutSupported) {
            val pinShortcutInfoBuilder = ShortcutInfo.Builder(context, list.get(position).getName())
            pinShortcutInfoBuilder.setShortLabel(list.get(position).getName())
            val intent =
                context.packageManager.getLaunchIntentForPackage(list.get(position).getPackages())
            pinShortcutInfoBuilder.setIntent(intent!!)
            pinShortcutInfoBuilder.setIcon(Icon.createWithResource(context, R.drawable.ic_lock))
            val pinShortcutInfo = pinShortcutInfoBuilder.build()

            val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(
                pinShortcutInfo
            )
            val successCallback = PendingIntent.getBroadcast(
                context, 0,
                pinnedShortcutCallbackIntent, 0
            )
            shortcutManager.requestPinShortcut(
                pinShortcutInfo,
                successCallback.intentSender
            )
        }
    }
}