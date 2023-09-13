package com.example.videoplayers2

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

open class OnSwipeTouchListener(context: Context) : View.OnTouchListener {

    private lateinit var gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    @Override
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event!!)
    }

    class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            onSingleTouch()
            return super.onSingleTapConfirmed(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            onDoubleTouch()
            return super.onDoubleTap(e)
        }

        fun onDoubleTouch() {
        }

        fun onSingleTouch() {
        }
    }

    public fun onDoubleTouch() {
    }

    public fun onSingleTouch() {
    }

}