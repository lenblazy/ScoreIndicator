package com.lenibonje.scoreindicator.utils

import com.lenibonje.scoreindicator.utils.Constants.ARC_WIDTH_MULTIPLIER
import com.lenibonje.scoreindicator.utils.Constants.BIG_DOLLAR_SIZE_MULTIPLIER
import com.lenibonje.scoreindicator.utils.Constants.DOT_SIZE_MULTIPLIER
import com.lenibonje.scoreindicator.utils.Constants.LINE_WIDTH_MULTIPLIER
import com.lenibonje.scoreindicator.utils.Constants.SMALL_DOLLAR_SIZE_MULTIPLIER
import com.lenibonje.scoreindicator.utils.Constants.START_X
import com.lenibonje.scoreindicator.utils.Constants.STICK_LENGTH_MULTIPLIER
import com.lenibonje.scoreindicator.utils.Constants.STROKE_WIDTH_MULTIPLIER
import com.lenibonje.scoreindicator.utils.Constants.TEXT_SIZE_MULTIPLIER
import com.lenibonje.scoreindicator.utils.Constants.WIDGET_HEIGHT
import com.lenibonje.scoreindicator.utils.Constants.WIDGET_WIDTH

object GlobalVars {

    var widgetWidth: Int = WIDGET_WIDTH
    var widgetHeight: Float = WIDGET_HEIGHT.toFloat()

    var startX: Float = START_X

    var textSize = widgetWidth * TEXT_SIZE_MULTIPLIER
    var strokeWidth: Float = widgetWidth * STROKE_WIDTH_MULTIPLIER

    var bigDollarSize: Float = widgetWidth * BIG_DOLLAR_SIZE_MULTIPLIER
    var smallDollarSize = widgetWidth * SMALL_DOLLAR_SIZE_MULTIPLIER

    var arcWidth: Float = widgetWidth * ARC_WIDTH_MULTIPLIER
    var dotSize: Float = widgetWidth * DOT_SIZE_MULTIPLIER

    var lineWidth: Float = widgetWidth * LINE_WIDTH_MULTIPLIER

    var stickLength: Float = widgetHeight * STICK_LENGTH_MULTIPLIER
}
