package com.lenibonje.scoreindicator.utils

import android.content.res.Resources


class ScreenComputations(private val density: Float) {

    fun dpToPx(size: Double): Float {
        return (size * density).toFloat()
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }
}
