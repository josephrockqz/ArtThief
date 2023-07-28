package com.example.artthief.ar.kotlin

import android.content.res.Resources
import android.opengl.GLSurfaceView
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.artthief.R
import com.example.artthief.ar.java.common.helpers.SnackbarHelper
import com.example.artthief.ar.java.common.helpers.TapHelper

class ArView(val activity: ArActivity) : DefaultLifecycleObserver {

    val root: View = View.inflate(activity, R.layout.activity_ar, null)
    val surfaceView: GLSurfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceView)
    val settingsButton =
        root.findViewById<ImageButton>(R.id.ib_arInfoButton).apply {
            setOnClickListener { v ->
//                PopupMenu(activity, v).apply {
//                    setOnMenuItemClickListener { item ->
//                        when (item.itemId) {
//                            else -> null
//                        } != null
//                    }
//                    inflate(R.menu.augmented_settings_menu)
//                    show()
//                }
            }
        }

    val snackbarHelper = SnackbarHelper()
    val tapHelper = TapHelper(activity).also { surfaceView.setOnTouchListener(it) }

    override fun onResume(owner: LifecycleOwner) {
        surfaceView.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        surfaceView.onPause()
    }
}
