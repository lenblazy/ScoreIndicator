package com.lenibonje.scoreindicator

import android.graphics.Canvas
import android.graphics.Paint

fun Canvas.drawSemicircle(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    paint: Paint,
    useCenter: Boolean = false
) {
    this.drawArc(
        left,
        top,
        right,
        bottom,
        Constants.ZERO,
        Constants.NEG_SEMI_CIRCLE,
        useCenter,
        paint
    )
}