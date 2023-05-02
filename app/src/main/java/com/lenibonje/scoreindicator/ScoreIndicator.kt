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
    private var stickRectF = RectF()

    private var bigDollar: Bitmap
    private var smallDollar: Bitmap
    private var stick: Bitmap

    init {
        context.theme.obtainStyledAttributes(
            attributeSet, R.styleable.score_indicator, 0, 0
        ).apply {
            try {
                paint.color = getColor(R.styleable.score_indicator_good_score, Color.GREEN)
                stickPaint.color = getColor(R.styleable.score_indicator_stick_color, Color.BLACK)
            } finally {
                recycle()
            }

        }
        grayPaint.apply {
            color = Color.LTGRAY
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
//        stick = Bitmap.createScaledBitmap(stick, 90, 200, false)

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

        canvas?.drawLine(
            centerX - outerMostRadius + 5,
            centerY,
            centerX - innerMostRadius - 5,
            centerY,
            blackPaint
        )

        canvas?.drawLine(
            centerX + outerMostRadius - 5,
            centerY ,
            centerX + innerMostRadius + 5,
            centerY,
            blackPaint
        )

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

        canvas?.drawBitmap(
            smallDollar,
            centerX + outerMostRadius - 40f,
            centerY - outerMostRadius,
            null
        )
        canvas?.drawBitmap(
            bigDollar,
            centerX + outerMostRadius + 30f,
            centerY - outerMostRadius + 120f,
            null
        )

        canvas?.drawCircle(centerX, centerY - 20f, 20f, stickPaint)

        stickRectF.left = centerX - 10f
        stickRectF.top = centerY - 30f
        stickRectF.right = centerX + innerMostRadius + 50f
        stickRectF.bottom = centerY - 10f

        val radius = 10f
        canvas?.drawRoundRect(stickRectF, radius, radius, stickPaint)

    }

}