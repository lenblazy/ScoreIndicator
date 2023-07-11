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
import com.lenibonje.scoreindicator.utils.GlobalVars.startX
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

    // dp sizes
    private val oneDp: Float
        get() = screenComputations.dpToPx(1.0)

    private val twoDp: Float
        get() = screenComputations.dpToPx(2.0)

    private val threeDp: Float
        get() = screenComputations.dpToPx(3.0)

    private val fourDp: Float
        get() = screenComputations.dpToPx(4.0)

    private val fiveDp: Float
        get() = screenComputations.dpToPx(5.0)

    private val thirtyDp: Float
        get() = screenComputations.dpToPx(30.0)


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

        }

        blackPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = oneDp
            isAntiAlias = true
        }

        bigDollar = BitmapFactory.decodeResource(resources, R.drawable.big_dollar)
        smallDollar = BitmapFactory.decodeResource(resources, R.drawable.small_dollar)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth =
            screenComputations.dpToPx(
                WIDGET_WIDTH.toDouble() + paddingStart + paddingEnd
            ).toInt()

        widgetWidth = measureDimension(desiredWidth, widthMeasureSpec)
        widgetHeight = (widgetWidth / 2.6F) + paddingStart + paddingEnd

        calculateDimensions(widgetWidth)

        setMeasuredDimension(
            widgetWidth,
            widgetHeight.toInt() + startX.toInt()
        )
    }

    private fun calculateDimensions(desiredWidth: Int) {
        textSize = desiredWidth * 0.045
        textPaint.textSize = textSize.toFloat()

        strokeWidth = desiredWidth * 0.025F
        grayPaint.strokeWidth = strokeWidth

        bigDollarSize = desiredWidth * 0.2
        bigDollar = Bitmap.createScaledBitmap(
            bigDollar,
            bigDollarSize.toInt(),
            bigDollarSize.toInt(),
            false
        )

        smallDollarSize = desiredWidth * 0.15
        smallDollar =
            Bitmap.createScaledBitmap(
                smallDollar,
                smallDollarSize.toInt(),
                smallDollarSize.toInt(),
                false
            )

        arcWidth = desiredWidth * 0.75F
        dotSize = desiredWidth * 0.03
        stickLength = desiredWidth * 0.15

        lineWidth = desiredWidth * 0.18

        startX = screenComputations.dpToPx(START_X)
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        val screenWidth = screenComputations.getScreenWidth()
        if (specMode == MeasureSpec.EXACTLY) {
            result = min(specSize, screenWidth)
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = min(result, screenWidth)
            }
        }

        return result
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

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
                left = startX,
                top = startX,
                right = arcWidth,
                bottom = (widgetHeight * 2),
                paint = grayPaint
            )

            // outer arc
            drawSemicircle(
                left = (startX + oneDp),
                top = (startX + oneDp),
                right = (arcWidth - oneDp),
                bottom = (widgetHeight * 2),
                paint = blackPaint,
                useCenter = true
            )

            // Draw the segments
            val segmentAngle = 180F / -NUM_OF_SEGMENTS
            for (i in 0 until NUM_OF_SEGMENTS) {
                paint.color = segmentColors[i]
                drawArc(
                    (startX + twoDp),
                    (startX + twoDp),
                    (arcWidth - twoDp),
                    (widgetHeight * 2 - twoDp),
                    i * segmentAngle,
                    segmentAngle,
                    true,
                    paint
                )
            }

            // inner shadow
            drawSemicircle(
                left = (startX + fiveDp + lineWidth).toFloat(),
                top = (startX + fiveDp + lineWidth).toFloat(),
                right = (arcWidth - fiveDp - lineWidth).toFloat(),
                bottom = (widgetHeight * 2 - fiveDp - lineWidth).toFloat(),
                paint = grayPaint
            )

            // inner arc
            drawSemicircle(
                left = (startX + lineWidth).toFloat(),
                top = (startX + lineWidth).toFloat(),
                right = (arcWidth - lineWidth).toFloat(),
                bottom = (widgetHeight * 2 - lineWidth).toFloat(),
                paint = blackPaint
            )

            // colorless shadow
            drawSemicircle(
                left = (startX + fourDp + lineWidth).toFloat(),
                top = (startX + fourDp + lineWidth).toFloat(),
                right = (arcWidth - fourDp - lineWidth).toFloat(),
                bottom = (widgetHeight * 2 - fourDp - lineWidth).toFloat(),
                paint = colorlessPaint
            )

            // colorless line
            drawLine(
                (startX + twoDp + lineWidth).toFloat(),
                (widgetHeight + dotSize).toFloat(),
                (arcWidth - twoDp - lineWidth).toFloat(),
                (widgetHeight + dotSize).toFloat(),
                colorlessPaint
            )

            // GOOD text
            drawText(
                GOOD_SCORE,
                (arcWidth - lineWidth + fiveDp).toFloat(),
                (widgetHeight - fiveDp * 2),
                textPaint
            )

            // Bad text
            drawText(
                BAD_SCORE,
                (startX + fiveDp * 2),
                (widgetHeight - fiveDp * 2),
                textPaint
            )

            // smaller dollar
            drawBitmap(
                smallDollar,
                (arcWidth * 0.9).toFloat(),
                startX,
                null
            )

            // bigger dollar
            drawBitmap(
                bigDollar,
                (arcWidth * 1.1).toFloat(),
                (widgetHeight * 0.7).toFloat(),
                null
            )

            // indicator circle
            drawCircle(
                (arcWidth / 2),
                (widgetHeight),
                (dotSize).toFloat(),
                stickPaint
            )

            // stick path
            stickPath.apply {
                moveTo(
                    (arcWidth / 2),
                    (widgetHeight + dotSize / 2).toFloat()
                )
                lineTo(
                    (arcWidth / 2 - stickLength).toFloat(),
                    (widgetHeight + dotSize / 2).toFloat()
                )

                arcTo(
                    (arcWidth / 2 - stickLength - thirtyDp).toFloat(),
                    (widgetHeight - dotSize / 2).toFloat(),
                    (arcWidth / 2 - stickLength).toFloat(),
                    (widgetHeight + dotSize / 2).toFloat(),
                    90F,
                    180F,
                    false
                )

                lineTo(
                    (arcWidth / 2),
                    (widgetHeight - dotSize / 2).toFloat()
                )
            }
            // Save the current canvas state
            save()
            // Set the pivot point for rotation
            rotate(
                percent,
                (arcWidth / 2),
                (widgetHeight)
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
