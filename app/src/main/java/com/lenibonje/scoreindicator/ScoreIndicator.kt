package com.lenibonje.scoreindicator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class ScoreIndicator(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {

    var paint = Paint()

//    private lateinit var bigDollar: Bitmap
//    private val smallDollar: Bitmap
//    private val stick: Bitmap

    init {
        context.theme.obtainStyledAttributes(
            attributeSet, R.styleable.score_indicator, 0, 0
        ).apply {
            try {
                paint.color = getColor(R.styleable.score_indicator_good_score, Color.GREEN)
            }finally {
                recycle()
            }

        }

//        bigDollar = BitmapFactory.decodeResource(resources, R.drawable.big_dollar)
//        smallDollar = BitmapFactory.decodeResource(resources, R.drawable.free_money)
//        stick = BitmapFactory.decodeResource(resources, R.drawable.stick)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = 90f

        canvas?.drawCircle(centerX, centerY, radius, paint)

//        canvas?.drawBitmap(smallDollar, width - 10f, height - 10f, null)
    }

}