package com.lenibonje.scoreindicator

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.lenibonje.scoreindicator.utils.Constants
import com.lenibonje.scoreindicator.utils.Constants.BAD_SCORE
import com.lenibonje.scoreindicator.utils.Constants.GOOD_SCORE
import com.lenibonje.scoreindicator.utils.Constants.NUM_OF_SEGMENTS
import com.lenibonje.scoreindicator.utils.Constants.START_X
import com.lenibonje.scoreindicator.utils.Constants.TEXT_SHADOW_SIZE
import com.lenibonje.scoreindicator.utils.Constants.WIDGET_WIDTH
import com.lenibonje.scoreindicator.utils.Constants.ZERO
import com.lenibonje.scoreindicator.utils.GlobalVars
import com.lenibonje.scoreindicator.utils.GlobalVars.arcWidth
import com.lenibonje.scoreindicator.utils.GlobalVars.bigDollarSize
import com.lenibonje.scoreindicator.utils.GlobalVars.dotSize
import com.lenibonje.scoreindicator.utils.GlobalVars.lineWidth
import com.lenibonje.scoreindicator.utils.GlobalVars.smallDollarSize
import com.lenibonje.scoreindicator.utils.GlobalVars.stickLength
import com.lenibonje.scoreindicator.utils.GlobalVars.strokeWidth
import com.lenibonje.scoreindicator.utils.GlobalVars.textSize
import com.lenibonje.scoreindicator.utils.GlobalVars.widgetHeight
import com.lenibonje.scoreindicator.utils.GlobalVars.widgetWidth
import com.lenibonje.scoreindicator.utils.ScreenComputations
import com.lenibonje.scoreindicator.utils.drawSemicircle
import kotlin.math.min

class ScoreIndicator(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    private var paint = Paint()
    private var grayPaint = Paint()
    private var blackPaint = Paint()
    private var stickPaint = Paint()
    private var textPaint = Paint()
    private var colorlessPaint = Paint()

    private var bigDollar: Bitmap
    private var smallDollar: Bitmap

    private var score = ZERO
    var percent: Float = 0F
    var animateDuration: Int = Constants.ANIMATION_DURATION
    var animate: Boolean = true

    private val stickPath = Path()

    private val screenComputations by lazy {
        ScreenComputations(density = resources.displayMetrics.density)
    }

    private val lightThemeColor: Int
    private val darkThemeColor: Int

    private var segmentColors: IntArray = intArrayOf(
        Color.parseColor("#2F6C00"),
        Color.parseColor("#3F8709"),
        Color.parseColor("#72BE42"),
        Color.parseColor("#8CDA5B"),
        Color.parseColor("#CEFFAB")
    )

    init {

        lightThemeColor = ContextCompat.getColor(context, R.color.lightThemeColor)
        darkThemeColor = ContextCompat.getColor(context, R.color.darkThemeColor)

        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.score_indicator,
            0,
            0
        ).apply {
            try {
                animate = getBoolean(R.styleable.score_indicator_animate, true)

                animateDuration = getInteger(
                    R.styleable.score_indicator_animationDuration,
                    Constants.ANIMATION_DURATION
                )

                score = getFloat(R.styleable.score_indicator_score, ZERO)
                if (animate) {
                    animateRotation(score, animateDuration.toLong())
                } else {
                    percent = score
                }

                paint.color = getColor(R.styleable.score_indicator_goodScore, Color.GREEN)

                stickPaint.apply {
                    color = getColor(R.styleable.score_indicator_stickColor, Color.BLACK)
                    isAntiAlias = true
                }

                colorlessPaint.apply {
                    color = Color.WHITE
                    strokeWidth = screenComputations.dpToPx(GlobalVars.strokeWidth * 1.6)
                    isAntiAlias = true
                }

                textPaint.apply {
                    color = getColor(R.styleable.score_indicator_textColor, Color.WHITE)
                    textSize = screenComputations.dpToPx(GlobalVars.textSize)
                    isFakeBoldText = true
                    isAntiAlias = true
                    setShadowLayer(
                        screenComputations.dpToPx(TEXT_SHADOW_SIZE),
                        screenComputations.dpToPx(TEXT_SHADOW_SIZE),
                        screenComputations.dpToPx(TEXT_SHADOW_SIZE),
                        Color.BLACK
                    )
                }
            } finally {
                recycle()
            }
        }

        grayPaint.apply {
            isAntiAlias = true
            color = Color.parseColor("#E0E0E0")
            style = Paint.Style.STROKE
            strokeWidth = screenComputations.dpToPx(GlobalVars.strokeWidth)
        }

        blackPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = screenComputations.dpToPx(1.0)
            isAntiAlias = true
        }

        bigDollar = BitmapFactory.decodeResource(resources, R.drawable.big_dollar)
        bigDollar = Bitmap.createScaledBitmap(
            bigDollar,
            screenComputations.dpToPx(bigDollarSize).toInt(),
            screenComputations.dpToPx(bigDollarSize).toInt(),
            false
        )
        smallDollar = BitmapFactory.decodeResource(resources, R.drawable.small_dollar)
        smallDollar =
            Bitmap.createScaledBitmap(
                smallDollar,
                screenComputations.dpToPx(smallDollarSize).toInt(),
                screenComputations.dpToPx(smallDollarSize).toInt(),
                false
            )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth =
            screenComputations.dpToPx(
                WIDGET_WIDTH.toDouble() + paddingStart + paddingEnd
            ).toInt()

        widgetWidth = measureDimension(desiredWidth, widthMeasureSpec)
        Log.d("Score Indicator", "onMeasure: widgetWidth: $widgetWidth")
        widgetHeight = widgetWidth / 2.6

        calculateDimensions(widgetWidth)


        setMeasuredDimension(
            widgetWidth,
            widgetHeight.toInt()
        )
    }

    private fun calculateDimensions(desiredWidth: Int) {
        textSize = desiredWidth * 0.045
        textPaint.textSize = screenComputations.dpToPx(textSize)

        strokeWidth = desiredWidth * 0.025

        bigDollarSize = desiredWidth * 0.2
        bigDollar = Bitmap.createScaledBitmap(
            bigDollar,
            screenComputations.dpToPx(bigDollarSize).toInt(),
            screenComputations.dpToPx(bigDollarSize).toInt(),
            false
        )

        smallDollarSize = desiredWidth * 0.15
        smallDollar =
            Bitmap.createScaledBitmap(
                smallDollar,
                screenComputations.dpToPx(smallDollarSize).toInt(),
                screenComputations.dpToPx(smallDollarSize).toInt(),
                false
            )

        arcWidth = desiredWidth * 0.8
        dotSize = desiredWidth * 0.03
        stickLength = desiredWidth * 0.25

        lineWidth = desiredWidth * 0.18
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = min(result, specSize)
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.d("Score", "onDraw: widgetHeight $widgetHeight")
        Log.d("Score", "onDraw: widgetWidth $widgetWidth")
        Log.d(
            "Score",
            "onDraw: widgetHeight is less than or equal to 2.6 widgetWidth?= ${widgetHeight <= widgetWidth / 2.6}"
        )


        val currentNightMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        val themeColor = if (isDarkTheme) darkThemeColor else lightThemeColor

        colorlessPaint.color = themeColor
        val whiteColor = if (isDarkTheme) lightThemeColor else darkThemeColor

        stickPaint.color = whiteColor
        textPaint.color = themeColor

        // method requires api level 21
        canvas?.apply {
            // outer shadow
            drawSemicircle(
                left = screenComputations.dpToPx(START_X),
                top = screenComputations.dpToPx(START_X),
                right = screenComputations.dpToPx(arcWidth),
                bottom = screenComputations.dpToPx(widgetHeight * 2),
                paint = grayPaint
            )
            // outer arc
            drawSemicircle(
                left = screenComputations.dpToPx(START_X + 1),
                top = screenComputations.dpToPx(START_X + 1),
                right = screenComputations.dpToPx(arcWidth - 1),
                bottom = screenComputations.dpToPx(widgetHeight * 2),
                paint = blackPaint,
                useCenter = true
            )

            // Draw the segments
            val segmentAngle = 180F / -NUM_OF_SEGMENTS
            for (i in 0 until NUM_OF_SEGMENTS) {
                paint.color = segmentColors[i]
                drawArc(
                    screenComputations.dpToPx(START_X + 2),
                    screenComputations.dpToPx(START_X + 2),
                    screenComputations.dpToPx(arcWidth - 2),
                    screenComputations.dpToPx(widgetHeight * 2 - 2),
                    i * segmentAngle,
                    segmentAngle,
                    true,
                    paint
                )
            }

            // inner shadow
            drawSemicircle(
                left = screenComputations.dpToPx(START_X + 3 + lineWidth),
                top = screenComputations.dpToPx(START_X + 3 + lineWidth),
                right = screenComputations.dpToPx(arcWidth - 3 - lineWidth),
                bottom = screenComputations.dpToPx(widgetHeight * 2 - 3 - lineWidth),
                paint = grayPaint
            )

            // inner arc
            drawSemicircle(
                left = screenComputations.dpToPx(START_X + 1 + lineWidth),
                top = screenComputations.dpToPx(START_X + 1 + lineWidth),
                right = screenComputations.dpToPx(arcWidth - 1 - lineWidth),
                bottom = screenComputations.dpToPx(widgetHeight * 2 - 1 - lineWidth),
                paint = blackPaint
            )

            // colorless shadow
            drawSemicircle(
                left = screenComputations.dpToPx(START_X + 4 + lineWidth),
                top = screenComputations.dpToPx(START_X + 4 + lineWidth),
                right = screenComputations.dpToPx(arcWidth - 4 - lineWidth),
                bottom = screenComputations.dpToPx(widgetHeight * 2 - 4 - lineWidth),
                paint = colorlessPaint
            )

            // colorless line
            drawLine(
                screenComputations.dpToPx(START_X + 2 + lineWidth),
                screenComputations.dpToPx(widgetHeight + dotSize),
                screenComputations.dpToPx(arcWidth - 2 - lineWidth),
                screenComputations.dpToPx(widgetHeight + dotSize),
                colorlessPaint
            )

            // GOOD text
            drawText(
                GOOD_SCORE,
                screenComputations.dpToPx(arcWidth - lineWidth),
                screenComputations.dpToPx(widgetHeight - 3),
                textPaint
            )

            // Bad text
            drawText(
                BAD_SCORE,
                screenComputations.dpToPx(START_X + 5),
                screenComputations.dpToPx(widgetHeight - 3),
                textPaint
            )

            // smaller dollar
            drawBitmap(
                smallDollar,
                screenComputations.dpToPx(arcWidth * 0.9),
                screenComputations.dpToPx(START_X),
                null
            )

            // bigger dollar
            drawBitmap(
                bigDollar,
                screenComputations.dpToPx(arcWidth * 1.1),
                screenComputations.dpToPx(widgetHeight * 0.7),
                null
            )

            // indicator circle
            drawCircle(
                screenComputations.dpToPx(arcWidth / 2),
                screenComputations.dpToPx(widgetHeight),
                screenComputations.dpToPx(dotSize),
                stickPaint
            )
            // stick path
            stickPath.apply {
                moveTo(
                    screenComputations.dpToPx(arcWidth / 2),
                    screenComputations.dpToPx(widgetHeight + dotSize / 2)
                )
                lineTo(
                    screenComputations.dpToPx(arcWidth / 2 - stickLength),
                    screenComputations.dpToPx(widgetHeight + dotSize / 2)
                )

                // public void arcTo(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean forceMoveTo)
                arcTo(
                    screenComputations.dpToPx(arcWidth / 2 - stickLength - 30),
                    screenComputations.dpToPx(widgetHeight - dotSize / 2),
                    screenComputations.dpToPx(arcWidth / 2 - stickLength),
                    screenComputations.dpToPx(widgetHeight + dotSize / 2),
                    90F,
                    180F,
                    false
                )

                lineTo(
                    screenComputations.dpToPx(arcWidth / 2),
                    screenComputations.dpToPx(widgetHeight - dotSize / 2)
                )
            }
            // Save the current canvas state
            save()
            // Set the pivot point for rotation
            rotate(
                percent,
                screenComputations.dpToPx(arcWidth / 2),
                screenComputations.dpToPx(widgetHeight)
            )
            drawPath(stickPath, stickPaint)
            restore()
        }
    }

    private fun animateRotation(degrees: Float, duration: Long) {
        val animator = ValueAnimator.ofFloat(percent, degrees)
        animator.addUpdateListener { valueAnimator ->
            percent = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.duration = duration
        animator.start()
    }
}
