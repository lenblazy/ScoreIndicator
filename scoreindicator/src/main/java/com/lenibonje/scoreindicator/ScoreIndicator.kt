package com.lenibonje.scoreindicator

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.lenibonje.scoreindicator.utils.Constants
import com.lenibonje.scoreindicator.utils.Constants.ARC_WIDTH
import com.lenibonje.scoreindicator.utils.Constants.BAD_SCORE
import com.lenibonje.scoreindicator.utils.Constants.DOT_SIZE
import com.lenibonje.scoreindicator.utils.Constants.GOOD_SCORE
import com.lenibonje.scoreindicator.utils.Constants.LINE_WIDTH
import com.lenibonje.scoreindicator.utils.Constants.NUM_OF_SEGMENTS
import com.lenibonje.scoreindicator.utils.Constants.START_X
import com.lenibonje.scoreindicator.utils.Constants.STICK_LENGTH
import com.lenibonje.scoreindicator.utils.Constants.STROKE_WIDTH
import com.lenibonje.scoreindicator.utils.Constants.TEXT_SHADOW_SIZE
import com.lenibonje.scoreindicator.utils.Constants.TEXT_SIZE
import com.lenibonje.scoreindicator.utils.Constants.WIDGET_HEIGHT
import com.lenibonje.scoreindicator.utils.Constants.ZERO
import com.lenibonje.scoreindicator.utils.GlobalVars.arcWidth
import com.lenibonje.scoreindicator.utils.GlobalVars.bigDollarSize
import com.lenibonje.scoreindicator.utils.GlobalVars.dotSize
import com.lenibonje.scoreindicator.utils.GlobalVars.lineWidth
import com.lenibonje.scoreindicator.utils.GlobalVars.smallDollarSize
import com.lenibonje.scoreindicator.utils.GlobalVars.strokeWidth
import com.lenibonje.scoreindicator.utils.GlobalVars.textSize
import com.lenibonje.scoreindicator.utils.GlobalVars.widgetHeight
import com.lenibonje.scoreindicator.utils.GlobalVars.widgetWidth
import com.lenibonje.scoreindicator.utils.ScreenComputations
import com.lenibonje.scoreindicator.utils.drawSemicircle


class ScoreIndicator(context: Context, attributeSet: AttributeSet?)
    : View(context, attributeSet) {

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

    private val screenComputations = ScreenComputations(density = resources.displayMetrics.density)

    private var segmentColors: IntArray = intArrayOf(
        Color.parseColor("#2F6C00"),
        Color.parseColor("#3F8709"),
        Color.parseColor("#72BE42"),
        Color.parseColor("#8CDA5B"),
        Color.parseColor("#CEFFAB")
    )

    init {
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
                if (animate) animateRotation(score, animateDuration.toLong())
                else percent = score

                paint.color = getColor(R.styleable.score_indicator_goodScore, Color.GREEN)

                stickPaint.apply {
                    color = getColor(R.styleable.score_indicator_stickColor, Color.BLACK)
                    isAntiAlias = true
                }

                colorlessPaint.apply {
                    color = Color.WHITE
                    strokeWidth = screenComputations.dpToPx(STROKE_WIDTH * 1.6)
                    isAntiAlias = true
                }

                textPaint.apply {
                    color = getColor(R.styleable.score_indicator_textColor, Color.WHITE)
                    textSize = screenComputations.dpToPx(TEXT_SIZE)
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
            strokeWidth = screenComputations.dpToPx(STROKE_WIDTH)
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
            screenComputations.dpToPx(widgetWidth.toDouble() + paddingStart + paddingEnd).toInt()
        val desiredHeight =
            screenComputations.dpToPx(widgetHeight + paddingTop + paddingBottom + 10).toInt()

        calculateDimensions(desiredWidth)

        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )

    }

    private fun calculateDimensions(desiredWidth: Int) {
        textSize = desiredWidth * 0.045
        strokeWidth = desiredWidth * 0.025

        bigDollarSize = desiredWidth * 0.2
        smallDollarSize = desiredWidth * 0.15

        arcWidth = desiredWidth * 0.75
        dotSize = desiredWidth * 0.03

        lineWidth = desiredWidth * 0.18

//        var stickLength = widgetHeight * 0.1
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
                result = Math.min(result, specSize)
            }
        }
        if (result < desiredSize) {
            Log.e("ChartView", "The view is too small, the content might get cut")
        }
        return result
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // method requires api level 21
        canvas?.apply {
            // outer shadow
            drawSemicircle(
                left = screenComputations.dpToPx(START_X),
                top = screenComputations.dpToPx(START_X),
                right = screenComputations.dpToPx(ARC_WIDTH),
                bottom = screenComputations.dpToPx(WIDGET_HEIGHT * 2),
                paint = grayPaint
            )
            // outer arc
            drawSemicircle(
                left = screenComputations.dpToPx(START_X + 1),
                top = screenComputations.dpToPx(START_X + 1),
                right = screenComputations.dpToPx(ARC_WIDTH - 1),
                bottom = screenComputations.dpToPx(WIDGET_HEIGHT.toDouble() * 2),
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
                    screenComputations.dpToPx(ARC_WIDTH - 2),
                    screenComputations.dpToPx(WIDGET_HEIGHT * 2 - 2),
                    i * segmentAngle,
                    segmentAngle,
                    true,
                    paint
                )
            }

            // inner shadow
            drawSemicircle(
                left = screenComputations.dpToPx(START_X + 3 + LINE_WIDTH),
                top = screenComputations.dpToPx(START_X + 3 + LINE_WIDTH),
                right = screenComputations.dpToPx(ARC_WIDTH - 3 - LINE_WIDTH),
                bottom = screenComputations.dpToPx(WIDGET_HEIGHT * 2 - 3 - LINE_WIDTH),
                paint = grayPaint
            )

            // inner arc
            drawSemicircle(
                left = screenComputations.dpToPx(START_X + 1 + LINE_WIDTH),
                top = screenComputations.dpToPx(START_X + 1 + LINE_WIDTH),
                right = screenComputations.dpToPx(ARC_WIDTH - 1 - LINE_WIDTH),
                bottom = screenComputations.dpToPx(WIDGET_HEIGHT * 2 - 1 - LINE_WIDTH),
                paint = blackPaint
            )

            // colorless shadow
            drawSemicircle(
                left = screenComputations.dpToPx(START_X + 4 + LINE_WIDTH),
                top = screenComputations.dpToPx(START_X + 4 + LINE_WIDTH),
                right = screenComputations.dpToPx(ARC_WIDTH - 4 - LINE_WIDTH),
                bottom = screenComputations.dpToPx(WIDGET_HEIGHT * 2 - 4 - LINE_WIDTH),
                paint = colorlessPaint
            )

            // colorless line
            drawLine(
                screenComputations.dpToPx(START_X + 2 + LINE_WIDTH),
                screenComputations.dpToPx(WIDGET_HEIGHT + DOT_SIZE),
                screenComputations.dpToPx(ARC_WIDTH - 2 - LINE_WIDTH),
                screenComputations.dpToPx(WIDGET_HEIGHT + DOT_SIZE),
                colorlessPaint
            )

            // GOOD text
            drawText(
                GOOD_SCORE,
                screenComputations.dpToPx(ARC_WIDTH - LINE_WIDTH),
                screenComputations.dpToPx(WIDGET_HEIGHT.toDouble() - 3),
                textPaint
            )

            // Bad text
            drawText(
                BAD_SCORE,
                screenComputations.dpToPx(START_X + 5),
                screenComputations.dpToPx(WIDGET_HEIGHT.toDouble() - 3),
                textPaint
            )

            // smaller dollar
            drawBitmap(
                smallDollar,
                screenComputations.dpToPx(ARC_WIDTH * 0.9),
                screenComputations.dpToPx(START_X),
                null
            )

            // bigger dollar
            drawBitmap(
                bigDollar,
                screenComputations.dpToPx(widgetWidth * 0.78),
                screenComputations.dpToPx(WIDGET_HEIGHT * 0.6),
                null
            )

            // indicator circle
            drawCircle(
                screenComputations.dpToPx(ARC_WIDTH / 2),
                screenComputations.dpToPx(WIDGET_HEIGHT),
                screenComputations.dpToPx(DOT_SIZE),
                stickPaint
            )
            // stick path
            stickPath.apply {
                moveTo(
                    screenComputations.dpToPx(ARC_WIDTH / 2),
                    screenComputations.dpToPx(WIDGET_HEIGHT + DOT_SIZE / 2)
                )
                lineTo(
                    screenComputations.dpToPx(ARC_WIDTH / 2 - STICK_LENGTH),
                    screenComputations.dpToPx(WIDGET_HEIGHT + DOT_SIZE / 2)
                )

                // public void arcTo(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean forceMoveTo)
                arcTo(
                    screenComputations.dpToPx(ARC_WIDTH / 2 - STICK_LENGTH - 30),
                    screenComputations.dpToPx(WIDGET_HEIGHT - DOT_SIZE / 2),
                    screenComputations.dpToPx(ARC_WIDTH / 2 - STICK_LENGTH),
                    screenComputations.dpToPx(WIDGET_HEIGHT + DOT_SIZE / 2),
                    90F,
                    180F,
                    false
                )

                lineTo(
                    screenComputations.dpToPx(ARC_WIDTH / 2),
                    screenComputations.dpToPx(WIDGET_HEIGHT - DOT_SIZE / 2)
                )
            }
            // Save the current canvas state
            save()
            // Set the pivot point for rotation
            rotate(
                percent,
                screenComputations.dpToPx(ARC_WIDTH / 2),
                screenComputations.dpToPx(WIDGET_HEIGHT.toDouble())
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
