package com.example.artthief.ar.kotlin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.artthief.ar.java.common.helpers.CameraPermissionHelper
import com.example.artthief.ar.java.common.helpers.FullScreenHelper
import com.example.artthief.ar.java.common.samplerender.SampleRender
import com.example.artthief.ar.kotlin.common.helpers.ARCoreSessionLifecycleHelper
import com.google.ar.core.Config
import com.google.ar.core.Config.InstantPlacementMode
import com.google.ar.core.Session
import com.google.ar.core.exceptions.*

class ArActivity : AppCompatActivity() {

    companion object {
        private const val SHARED_PREFERENCES_POINT_CLOUD_ENABLED = "point_cloud_enabled"
        private const val TAG = "ArActivity"
    }

    lateinit var arCoreSessionHelper: ARCoreSessionLifecycleHelper
    lateinit var view: ArView
    lateinit var renderer: ArRenderer

    private var sharedPreferences: SharedPreferences? = null
    private var pointCloudEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        pointCloudEnabled = getPreferences(Context.MODE_PRIVATE).getBoolean(
            SHARED_PREFERENCES_POINT_CLOUD_ENABLED,
            false
        )


        val artworkImageUri = intent.getStringExtra("artwork_image_url").toString()
        Log.i("artwork image uri ar activity", artworkImageUri)

        arCoreSessionHelper = ARCoreSessionLifecycleHelper(this)
        arCoreSessionHelper.exceptionCallback =
            { exception ->
                val message =
                    when (exception) {
                        is UnavailableUserDeclinedInstallationException ->
                            "Please install Google Play Services for AR"
                        is UnavailableApkTooOldException -> "Please update ARCore"
                        is UnavailableSdkTooOldException -> "Please update this app"
                        is UnavailableDeviceNotCompatibleException -> "This device does not support AR"
                        is CameraNotAvailableException -> "Camera not available. Try restarting the app."
                        else -> "Failed to create AR session: $exception"
                    }
                Log.e(TAG, "ARCore threw an exception", exception)
                view.snackbarHelper.showError(this, message)
            }

        // Configure session features, including: Lighting Estimation, Depth mode, Instant Placement.
        arCoreSessionHelper.beforeSessionResume = ::configureSession
        lifecycle.addObserver(arCoreSessionHelper)

        renderer = ArRenderer(this, artworkImageUri)
        lifecycle.addObserver(renderer)

        view = ArView(this)
        lifecycle.addObserver(view)
        setContentView(view.root)

        SampleRender(view.surfaceView, renderer, assets)
    }

    // Configure the session, using Lighting Estimation, and Depth mode.
    fun configureSession(session: Session) {
        session.configure(
            session.config.apply {
                lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR

                depthMode =
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        Config.DepthMode.AUTOMATIC
                    } else {
                        Config.DepthMode.DISABLED
                    }

                instantPlacementMode = InstantPlacementMode.DISABLED

                planeFindingMode = Config.PlaneFindingMode.VERTICAL
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            // Use toast instead of snackbar here since the activity will exit.
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                .show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
    }

    fun isPointCloudEnabled(): Boolean = pointCloudEnabled

    fun setPointCloudEnabled(enable: Boolean) {
        if (enable == pointCloudEnabled) {
            return
        }
        pointCloudEnabled = enable
        with (sharedPreferences!!.edit()) {
            putBoolean("point_cloud_enabled", pointCloudEnabled)
            apply()
        }
    }
}
