package com.example.artthief.common.helpers

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import java.util.concurrent.ArrayBlockingQueue

class TapHelper(context: Context) : OnTouchListener {

    private val gestureDetector = GestureDetector(
        context,
        object: GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                queuedSingleTaps.offer(e)
                return true
            }
            override fun onDown(e: MotionEvent): Boolean = true
    })
    private val queuedSingleTaps = ArrayBlockingQueue<MotionEvent>(16)

    fun poll(): MotionEvent {
        return queuedSingleTaps.poll()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event!!)
    }
}
