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
import android.view.View
import com.lenibonje.scoreindicator.Constants.ARC_WIDTH
import com.lenibonje.scoreindicator.Constants.BAD_SCORE
import com.lenibonje.scoreindicator.Constants.BIG_DOLLAR_SIZE
import com.lenibonje.scoreindicator.Constants.CENTER_X
import com.lenibonje.scoreindicator.Constants.CENTER_Y
import com.lenibonje.scoreindicator.Constants.DOT_SIZE
import com.lenibonje.scoreindicator.Constants.GOOD_SCORE
import com.lenibonje.scoreindicator.Constants.LINE_WIDTH
import com.lenibonje.scoreindicator.Constants.NUM_OF_SEGMENTS
import com.lenibonje.scoreindicator.Constants.SMALL_DOLLAR_SIZE
import com.lenibonje.scoreindicator.Constants.START_X
import com.lenibonje.scoreindicator.Constants.STROKE_WIDTH
import com.lenibonje.scoreindicator.Constants.TEXT_SHADOW_SIZE
import com.lenibonje.scoreindicator.Constants.TEXT_SIZE
import com.lenibonje.scoreindicator.Constants.WIDGET_HEIGHT
import com.lenibonje.scoreindicator.Constants.WIDGET_WIDTH
import com.lenibonje.scoreindicator.Constants.ZERO
import com.lenibonje.scoreindicator.utils.ScreenComputations
import java.lang.Integer.min

class ScoreIndicator(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {

    private var paint = Paint()
    private var grayPaint = Paint()
    private var blackPaint = Paint()
    private var innerBlackPaint = Paint()

    private var stickPaint = Paint()
    private var textPaint = Paint()
    private var colorlessPaint = Paint()

    private var bigDollar: Bitmap
    private var smallDollar: Bitmap

    private var score = ZERO
    var percent: Float = ZERO
    var animateDuration: Int = Constants.ANIMATION_DURATION
    var animate: Boolean = true

    private val stickPath = Path()

    private val screenComputations = ScreenComputations(density = resources.displayMetrics.density)

    private var segmentColors: IntArray = intArrayOf(
        Color.parseColor("#153800"),
        Color.parseColor("#225100"),
        Color.parseColor("#58A229"),
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
                if (animate) {
                    animateRotation(score, animateDuration.toLong())
                } else {
                    percent = score
                }
                paint.color = getColor(R.styleable.score_indicator_goodScore, Color.GREEN)
                stickPaint.color = getColor(R.styleable.score_indicator_stickColor, Color.BLACK)
                colorlessPaint.apply {
                    color = Color.WHITE
                    strokeWidth = screenComputations.dpToPx(1)
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
            color = Color.parseColor("#E0E0E0")
            style = Paint.Style.STROKE
            strokeWidth = STROKE_WIDTH
        }

        blackPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = screenComputations.dpToPx(1)
            isAntiAlias = true
        }

        innerBlackPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = screenComputations.dpToPx(LINE_WIDTH)
            isAntiAlias = true

        }

        bigDollar = BitmapFactory.decodeResource(resources, R.drawable.big_dollar)
        bigDollar = Bitmap.createScaledBitmap(bigDollar, BIG_DOLLAR_SIZE, BIG_DOLLAR_SIZE, false)
        smallDollar = BitmapFactory.decodeResource(resources, R.drawable.small_dollar)
        smallDollar =
            Bitmap.createScaledBitmap(smallDollar, SMALL_DOLLAR_SIZE, SMALL_DOLLAR_SIZE, false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            screenComputations.dpToPx(WIDGET_WIDTH+paddingStart+paddingEnd).toInt(),
            screenComputations.dpToPx((WIDGET_HEIGHT+paddingTop+paddingBottom) * 2).toInt(),
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // method requires api level 21
        canvas?.apply {
            setBackgroundColor(Color.WHITE)
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
                left = screenComputations.dpToPx(START_X + 1) ,
                top = screenComputations.dpToPx(START_X + 1) ,
                right = screenComputations.dpToPx(ARC_WIDTH - 1) ,
                bottom = screenComputations.dpToPx(WIDGET_HEIGHT * 2) ,
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
                paint = blackPaint,
            )

            // colorless shadow
            drawSemicircle(
                left = screenComputations.dpToPx(START_X + 3 + LINE_WIDTH),
                top = screenComputations.dpToPx(START_X + 3 + LINE_WIDTH) ,
                right = screenComputations.dpToPx(ARC_WIDTH - 3 - LINE_WIDTH),
                bottom = screenComputations.dpToPx(WIDGET_HEIGHT * 2 - 3 - LINE_WIDTH),
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
                screenComputations.dpToPx(ARC_WIDTH - 1 - LINE_WIDTH),
                screenComputations.dpToPx(WIDGET_HEIGHT - 3),
                textPaint
            )

            // Bad text
            drawText(
                BAD_SCORE,
                screenComputations.dpToPx(START_X + 5),
                screenComputations.dpToPx(WIDGET_HEIGHT - 3),
                textPaint
            )

//            // smaller dollar
//            drawBitmap(
//                smallDollar,
//                centerX + outerMostRadius - 40f,
//                centerY - outerMostRadius,
//                null
//            )
//            // bigger dollar
//            drawBitmap(
//                bigDollar,
//                centerX + outerMostRadius + 30f,
//                centerY - outerMostRadius + 120f,
//                null
//            )
            // indicator circle
            drawCircle(
                screenComputations.dpToPx(ARC_WIDTH / 2),
                screenComputations.dpToPx((WIDGET_HEIGHT * 2) / 2),
                screenComputations.dpToPx(DOT_SIZE),
                stickPaint)
            // stick path
//            stickPath.apply {
//                moveTo(centerX + 10, centerY - 10)
//                lineTo(centerX - innerMostRadius - 30, centerY - 10)
//                arcTo(
//                    centerX - innerMostRadius - 50,
//                    centerY - 20,
//                    centerX - innerMostRadius - 30,
//                    centerY - 10,
//                    90F,
//                    180F,
//                    false
//                )
//                lineTo(centerX + 10, centerY - 30)
//            }
//            // Save the current canvas state
//            save()
//            // Set the pivot point for rotation
//            rotate(percent, centerX, centerY - 20f)
//            drawPath(stickPath, stickPaint)
//            restore()
        }
    }

    fun animateRotation(degrees: Float, duration: Long) {
        val animator = ValueAnimator.ofFloat(percent, degrees)
        animator.addUpdateListener { valueAnimator ->
            percent = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.duration = duration
        animator.start()
    }
}
