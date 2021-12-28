package com.technado.applock

import android.os.Build

object AndroidUtils {
    private var RECENT_ACTIVITY: String? = null
    fun isRecentActivity(className: String?): Boolean {
        return if (RECENT_ACTIVITY.equals(className, ignoreCase = true)) {
            true
        } else false
    }

    init {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            RECENT_ACTIVITY = "com.android.systemui.recents.RecentsActivity"
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            RECENT_ACTIVITY = "com.android.systemui.recent.RecentsActivity"
        } else {
            RECENT_ACTIVITY = "com.android.internal.policy.impl.RecentApplicationDialog"
        }
    }
}