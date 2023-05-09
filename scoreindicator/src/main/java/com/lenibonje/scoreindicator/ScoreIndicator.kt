package com.lenibonje.scoreindicator

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.lenibonje.scoreindicator.Constants.BAD_SCORE
import com.lenibonje.scoreindicator.Constants.BIG_DOLLAR_SIZE
import com.lenibonje.scoreindicator.Constants.GOOD_SCORE
import com.lenibonje.scoreindicator.Constants.NEG_SEMI_CIRCLE
import com.lenibonje.scoreindicator.Constants.SMALL_DOLLAR_SIZE
import com.lenibonje.scoreindicator.Constants.STROKE_WIDTH
import com.lenibonje.scoreindicator.Constants.TEXT_SHADOW_SIZE
import com.lenibonje.scoreindicator.Constants.TEXT_SIZE
import com.lenibonje.scoreindicator.Constants.ZERO
import java.lang.Integer.min

class ScoreIndicator(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {

    private var paint = Paint()
    private var grayPaint = Paint()
    private var blackPaint = Paint()
    private var stickPaint = Paint()
    private var textPaint = Paint()
    private var colorlessPaint = Paint()

    private var bigDollar: Bitmap
    private var smallDollar: Bitmap

    private var score = Constants.ZERO
    var percent: Float = Constants.ZERO
    var animateDuration: Int = Constants.ANIMATION_DURATION
    var animate: Boolean = true

    private val stickPath = Path()

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
                score = getFloat(R.styleable.score_indicator_score, Constants.ZERO)
                if (animate) {
                    animateRotation(score, animateDuration.toLong())
                } else {
                    percent = score
                }
                paint.color = getColor(R.styleable.score_indicator_goodScore, Color.GREEN)
                stickPaint.color = getColor(R.styleable.score_indicator_stickColor, Color.BLACK)
                colorlessPaint.color = Color.WHITE
                textPaint.apply {
                    color = getColor(R.styleable.score_indicator_textColor, Color.WHITE)
                    textSize = TEXT_SIZE
                    isFakeBoldText = true
                    setShadowLayer(
                        TEXT_SHADOW_SIZE,
                        TEXT_SHADOW_SIZE,
                        TEXT_SHADOW_SIZE,
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
            strokeWidth = 2f
        }

        bigDollar = BitmapFactory.decodeResource(resources, R.drawable.big_dollar)
        bigDollar = Bitmap.createScaledBitmap(bigDollar, BIG_DOLLAR_SIZE, BIG_DOLLAR_SIZE, false)
        smallDollar = BitmapFactory.decodeResource(resources, R.drawable.small_dollar)
        smallDollar =
            Bitmap.createScaledBitmap(smallDollar, SMALL_DOLLAR_SIZE, SMALL_DOLLAR_SIZE, false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val outerMostRadius = min(width, height) / 5f
        val innerMostRadius = outerMostRadius - 100f

        // method requires api level 21
        // outer shadow
        canvas?.drawArc(
            centerX - outerMostRadius,
            centerY - outerMostRadius,
            centerX + outerMostRadius,
            centerY + outerMostRadius,
            ZERO,
            NEG_SEMI_CIRCLE,
            false,
            grayPaint
        )

        // outer arc
        canvas?.drawArc(
            centerX - outerMostRadius + 6f,
            centerY - outerMostRadius + 6f,
            centerX + outerMostRadius - 6f,
            centerY + outerMostRadius - 6f,
            ZERO,
            NEG_SEMI_CIRCLE,
            false,
            blackPaint
        )

        // left bottom line
        canvas?.drawLine(
            centerX - outerMostRadius + 5,
            centerY,
            centerX - innerMostRadius - 5,
            centerY,
            blackPaint
        )

        // right bottom line
        canvas?.drawLine(
            centerX + outerMostRadius - 5,
            centerY,
            centerX + innerMostRadius + 5,
            centerY,
            blackPaint
        )

        // mark Draw the segments
        val segmentAngle = 180f / -5f
        for (i in 0 until 5) {
            paint.color = segmentColors[i]
            canvas?.drawArc(
                centerX - outerMostRadius + 13f / 2f,
                centerY - outerMostRadius + 13f / 2f,
                centerX + outerMostRadius - 13f / 2f,
                centerY + outerMostRadius - 13f / 2f,
                i * segmentAngle,
                segmentAngle,
                true,
                paint
            )
        }

        // inner shadow
        canvas?.drawArc(
            centerX - innerMostRadius,
            centerY - innerMostRadius,
            centerX + innerMostRadius,
            centerY + innerMostRadius,
            ZERO,
            NEG_SEMI_CIRCLE,
            false,
            grayPaint
        )

        // inner arc
        canvas?.drawArc(
            centerX - innerMostRadius - 6f,
            centerY - innerMostRadius - 6f,
            centerX + innerMostRadius + 6f,
            centerY + innerMostRadius + 6f,
            ZERO,
            NEG_SEMI_CIRCLE,
            false,
            blackPaint
        )

        // colorless shadow
        canvas?.drawArc(
            centerX - innerMostRadius + 3f,
            centerY - innerMostRadius + 3f,
            centerX + innerMostRadius - 3f,
            centerY + innerMostRadius - 3f,
            ZERO,
            NEG_SEMI_CIRCLE,
            true,
            colorlessPaint
        )

        // GOOD text
        canvas?.drawText(
            GOOD_SCORE,
            centerX + innerMostRadius + 10f,
            centerY - 20f,
            textPaint
        )

        // Bad text
        canvas?.drawText(
            BAD_SCORE,
            centerX - outerMostRadius + 20f,
            centerY - 20f,
            textPaint
        )

        // smaller dollar
        canvas?.drawBitmap(
            smallDollar,
            centerX + outerMostRadius - 40f,
            centerY - outerMostRadius,
            null
        )

        // bigger dollar
        canvas?.drawBitmap(
            bigDollar,
            centerX + outerMostRadius + 30f,
            centerY - outerMostRadius + 120f,
            null
        )

        // indicator circle
        canvas?.drawCircle(centerX, centerY - 20f, 20f, stickPaint)

        // stick path
        stickPath.apply {
            moveTo(centerX + 10, centerY - 10)
            lineTo(centerX - innerMostRadius - 30, centerY - 10)
            arcTo(
                centerX - innerMostRadius - 50,
                centerY - 20,
                centerX - innerMostRadius - 30,
                centerY - 10,
                90F,
                180F,
                false
            )
            lineTo(centerX + 10, centerY - 30)
        }

        // Save the current canvas state
        canvas?.save()

//        // Set the pivot point for rotation
        canvas?.rotate(percent, centerX, centerY - 20f)

        canvas?.drawPath(stickPath, stickPaint)

        canvas?.restore()
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
