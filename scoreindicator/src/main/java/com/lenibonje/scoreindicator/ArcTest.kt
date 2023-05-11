package com.lenibonje.scoreindicator

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ArcTest(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var paint = Paint()
    val radius = 150
    val w = 200
    val pad = 5f

    private val shadowPaint = Paint(0).apply {
        color = 0x101010
        maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val h = radius / 2
//        setMeasuredDimension(w , h)
//    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Try for a width based on our minimum
//        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
//        val w: Int = View.resolveSizeAndState(minw, widthMeasureSpec, 1)

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
//        val minh: Int = View.MeasureSpec.getSize(w) + paddingBottom + paddingTop
//        val h: Int = View.resolveSizeAndState(minh, heightMeasureSpec, 0)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val weedth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> w.coerceAtMost(widthSize)
            else -> w
        }

        val heetth = when (widthMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> w.coerceAtMost(heightSize)
            else -> w
        }

        setMeasuredDimension(weedth, heetth)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawArc(
            0f + pad,
            0f + pad,
            radius - pad,
            radius - pad,
            0f,
            -180f,
            false,
            paint
        )
    }
}
