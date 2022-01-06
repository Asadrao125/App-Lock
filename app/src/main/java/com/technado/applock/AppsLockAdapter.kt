package com.technado.applock

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
        holder.itemView.setOnClickListener(View.OnClickListener {
            //Toast.makeText(context, "" + list.get(position).getName(), Toast.LENGTH_SHORT).show()

            addShortcutToHomeScreen(context, list, position)

        })
        holder.appName.text = list.get(position).getName()
        holder.image.setImageDrawable(list.get(position).getIcon())

        holder.imgLock.setOnClickListener(View.OnClickListener {
            //sharedPref.write("pkg-name", list.get(position).getPackages())
            //Toast.makeText(context, "" + sharedPref.read("pkg-name", ""), Toast.LENGTH_SHORT).show()
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appName: TextView
        var image: ImageView
        var imgLock: ImageView

        init {
            appName = itemView.findViewById(R.id.appName)
            image = itemView.findViewById(R.id.image)
            imgLock = itemView.findViewById(R.id.imgLock)
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
}