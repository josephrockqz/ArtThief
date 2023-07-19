package com.example.artthief.ar

import android.opengl.GLSurfaceView
import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.artthief.R
import com.example.artthief.common.helpers.TapHelper

class ArView(val activity: ArActivity) : DefaultLifecycleObserver {

    val root = View.inflate(activity, R.layout.activity_ar, null)
    val surfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceView)

    val session
        get() = activity.arCoreSessionHelper.session

    val tapHelper = TapHelper(activity)
        .also { surfaceView.setOnTouchListener(it) }

    override fun onResume(owner: LifecycleOwner) {
        surfaceView.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        surfaceView.onPause()
    }
}
