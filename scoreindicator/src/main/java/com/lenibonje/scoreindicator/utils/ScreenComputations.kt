package com.lenibonje.scoreindicator.utils

class ScreenComputations(private val density: Float) {

    fun dpToPx(size: Double): Float {
        return (size * density).toFloat()
    }
}
