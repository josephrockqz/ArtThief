package com.example.artthief.common.helpers

import android.app.Activity
import android.view.View

class FullScreenHelper {

    companion object {
        fun setFullScreenOnWindowFocusChanged(activity: Activity, hasFocus: Boolean) {
            if (hasFocus) {
                activity
                    .window
                    .decorView
                    .systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
        }
    }
}
