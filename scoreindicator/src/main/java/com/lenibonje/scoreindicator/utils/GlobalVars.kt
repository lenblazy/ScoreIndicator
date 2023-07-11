package com.lenibonje.scoreindicator.utils

import com.lenibonje.scoreindicator.utils.Constants.START_X
import com.lenibonje.scoreindicator.utils.Constants.WIDGET_HEIGHT
import com.lenibonje.scoreindicator.utils.Constants.WIDGET_WIDTH

object GlobalVars {

    var widgetWidth: Int = WIDGET_WIDTH
    var widgetHeight: Float = WIDGET_HEIGHT.toFloat()

    var startX: Float = START_X.toFloat()

    var textSize = widgetWidth * 0.045
    var strokeWidth: Float = widgetWidth * 0.025F

    var bigDollarSize = widgetWidth * 0.2
    var smallDollarSize = widgetWidth * 0.15

    var arcWidth: Float = widgetWidth * 0.75F
    var dotSize = widgetWidth * 0.03

    var lineWidth = widgetWidth * 0.18

    var stickLength = widgetHeight * 0.1
}
