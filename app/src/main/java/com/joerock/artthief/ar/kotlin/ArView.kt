package com.joerock.artthief.ar.kotlin

import android.opengl.GLSurfaceView
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.joerock.artthief.R
import com.joerock.artthief.ar.java.common.helpers.SnackbarHelper
import com.joerock.artthief.ar.java.common.helpers.TapHelper

class ArView(val activity: ArActivity) : DefaultLifecycleObserver {

    val root: View = View.inflate(activity, R.layout.activity_ar, null)
    val surfaceView: GLSurfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceView)

    val session
        get() = activity.arCoreSessionHelper.session

    val snackbarHelper = SnackbarHelper()
    val tapHelper = TapHelper(activity).also { surfaceView.setOnTouchListener(it) }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        configureButtonClickListeners()
    }

    override fun onResume(owner: LifecycleOwner) {
        surfaceView.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        surfaceView.onPause()
    }

    private fun configureButtonClickListeners() {
        val backButton = root.findViewById<ImageButton>(R.id.ib_arBackButton)
        backButton.apply {
            setOnClickListener {
                activity.finish()
            }
        }

        val settingsButton = root.findViewById<ImageButton>(R.id.ib_arInfoButton)
        settingsButton.apply {
            root.findViewById<ImageButton>(R.id.ib_arInfoButton).apply {
                setOnClickListener { v ->
                    PopupMenu(activity, v).apply {
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.point_cloud -> launchPointCloudSettingsMenuDialog()
                                R.id.vertical_plane_info -> launchVerticalPlaneDetectionInfoDialog()
                                else -> null
                            } != null
                        }
                        inflate(R.menu.augmented_settings_menu)
                        show()
                    }
                }
            }
        }
    }

    private fun launchPointCloudSettingsMenuDialog() {
        val resources = activity.resources
        val strings = resources.getStringArray(R.array.point_cloud_array)
        val checked = booleanArrayOf(activity.isPointCloudEnabled())
        AlertDialog.Builder(activity)
            .setTitle(R.string.options_title_point_cloud)
            .setMultiChoiceItems(strings, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton(R.string.done) { _, _ ->
                val session = session ?: return@setPositiveButton
                activity.setPointCloudEnabled(checked[0])
                activity.configureSession(session)
            }
            .show()
    }

    private fun launchVerticalPlaneDetectionInfoDialog() {
        AlertDialog.Builder(activity)
            .setTitle(R.string.vertical_plane_info)
            .setMessage(R.string.vertical_plane_info_description)
            .setPositiveButton(R.string.done) { _, _ ->
                val session = session ?: return@setPositiveButton
                activity.configureSession(session)
            }
            .show()
    }
}
