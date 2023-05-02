package com.lenibonje.scoreindicator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.lang.Integer.min


class ScoreIndicator(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {

    var paint = Paint()
    var grayPaint = Paint()
    var blackPaint = Paint()
    var stickPaint = Paint()
    var textPaint = Paint()

    private var bigDollar: Bitmap
    private var smallDollar: Bitmap
    private var stick: Bitmap
    private var percent: Float

    private val stickPath = Path()

    private var segmentColors: IntArray = intArrayOf(
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.YELLOW,
        Color.CYAN
    )

    init {
        context.theme.obtainStyledAttributes(
            attributeSet, R.styleable.score_indicator, 0, 0
        ).apply {
            try {
                percent = getFloat(R.styleable.score_indicator_score_percent, 0.0F)
                paint.color = getColor(R.styleable.score_indicator_good_score, Color.GREEN)
                stickPaint.color = getColor(R.styleable.score_indicator_stick_color, Color.BLACK)
                textPaint.apply {
                    color = getColor(R.styleable.score_indicator_text_color, Color.WHITE)
                    textSize = 25f
                    isFakeBoldText = true
                    setShadowLayer(
                        5f, 5f,
                        5f, Color.BLACK
                    )
                }

            } finally {
                recycle()
            }

        }
        grayPaint.apply {
            color = Color.parseColor("#E0E0E0")
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

        blackPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }

        bigDollar = BitmapFactory.decodeResource(resources, R.drawable.big_dollar)
        bigDollar = Bitmap.createScaledBitmap(bigDollar, 100, 100, false)
        smallDollar = BitmapFactory.decodeResource(resources, R.drawable.small_dollar)
        smallDollar = Bitmap.createScaledBitmap(smallDollar, 70, 70, false)
        stick = BitmapFactory.decodeResource(resources, R.drawable.stick)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val outerMostRadius = min(width, height) / 4f
        val innerMostRadius = outerMostRadius - 100f

        //method requires api level 21
        //outer shadow
        canvas?.drawArc(
            centerX - outerMostRadius,
            centerY - outerMostRadius,
            centerX + outerMostRadius,
            centerY + outerMostRadius,
            0f,
            -180f,
            false,
            grayPaint
        )

        //outer arc
        canvas?.drawArc(
            centerX - outerMostRadius + 6f,
            centerY - outerMostRadius + 6f,
            centerX + outerMostRadius - 6f,
            centerY + outerMostRadius - 6f,
            0f,
            -180f,
            false,
            blackPaint
        )

        //left bottom line
        canvas?.drawLine(
            centerX - outerMostRadius + 5,
            centerY,
            centerX - innerMostRadius - 5,
            centerY,
            blackPaint
        )

        //right bottom line
        canvas?.drawLine(
            centerX + outerMostRadius - 5,
            centerY,
            centerX + innerMostRadius + 5,
            centerY,
            blackPaint
        )

        //inner shadow
        canvas?.drawArc(
            centerX - innerMostRadius,
            centerY - innerMostRadius,
            centerX + innerMostRadius,
            centerY + innerMostRadius,
            0f,
            -180f,
            false,
            grayPaint
        )

        //inner arc
        canvas?.drawArc(
            centerX - innerMostRadius - 6f,
            centerY - innerMostRadius - 6f,
            centerX + innerMostRadius + 6f,
            centerY + innerMostRadius + 6f,
            0f,
            -180f,
            false,
            blackPaint
        )

        //mark Draw the segments
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
                false,
                paint
            )
        }

        //GOOD text
        canvas?.drawText(
            "GOOD",
            centerX + innerMostRadius + 20f,
            centerY - 20f,
            textPaint
        )

        //Bad text
        canvas?.drawText(
            "POOR",
            centerX - outerMostRadius + 20f,
            centerY - 20f,
            textPaint
        )

        //smaller dollar
        canvas?.drawBitmap(
            smallDollar,
            centerX + outerMostRadius - 40f,
            centerY - outerMostRadius,
            null
        )

        //bigger dollar
        canvas?.drawBitmap(
            bigDollar,
            centerX + outerMostRadius + 30f,
            centerY - outerMostRadius + 120f,
            null
        )

        //indicator circle
        canvas?.drawCircle(centerX, centerY - 20f, 20f, stickPaint)

        //stick path
        stickPath.apply {
            moveTo(centerX + 10, centerY - 10)
            lineTo(centerX + innerMostRadius, centerY - 10)
            arcTo(
                centerX + innerMostRadius,
                centerY - 20,
                centerX + 20 + innerMostRadius,
                centerY - 10,
                90F,
                -180F,
                false
            )
            lineTo(centerX + 10, centerY - 30)
        }

        canvas?.drawPath(stickPath, stickPaint)

    }

}