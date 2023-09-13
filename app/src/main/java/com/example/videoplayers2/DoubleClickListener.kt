package com.example.videoplayers2

import android.util.Log
import android.view.View

private var lastClicked = -1L

class DoubleClickListener(val callback: Callback) : View.OnClickListener {

    private var doubleClickTimeLimitMillis: Long = 500
    private var isSingleClick = false


    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onClick(v: View?) {
        Log.d("TAG", "onClick: ")
        val currentTime = System.currentTimeMillis()

        if (lastClicked == -1L || getTimeDiff(
                lastClicked,
                currentTime
            ) > doubleClickTimeLimitMillis
        ) {
            lastClicked = currentTime
            isSingleClick = true
            callback.singleClicked()
        } else if (isDoubleClicked(currentTime)) {
            callback.doubleClicked()
            lastClicked = -1L
            isSingleClick = false
        } else {
            Log.d("TAG", "onClick: else ")
            isSingleClick = true
            lastClicked = currentTime

        }
    }

    private fun getTimeDiff(from: Long, to: Long): Long {
        Log.d("TAG", "getTimeDiff: ")
        return to - from

    }

    private fun isDoubleClicked(currentTime: Long): Boolean {
        Log.d("TAG", "isDoubleClicked:")
        val done = getTimeDiff(
            lastClicked,
            currentTime
        ) <= doubleClickTimeLimitMillis
        return done

    }

    interface Callback {
        fun singleClicked()
        fun doubleClicked()

    }

}


