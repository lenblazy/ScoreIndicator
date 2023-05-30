package com.lenibonje.scoreindicator.utils

class ScreenComputations(private val density: Float) {

    fun dpToPx(size: Int): Float {
        return size * density
    }
}