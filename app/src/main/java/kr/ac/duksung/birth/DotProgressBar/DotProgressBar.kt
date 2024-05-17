package kr.ac.duksung.birth.DotProgressBar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DotProgressBar(context: Context, attrs: AttributeSet?): View(context, attrs) {
    private val dotRadius = 10f
    private val dotSpacing = 20f
    private val dotCount = 5
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        dotPaint.color = resources.getColor(android.R.color.black)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val totalWidth = dotCount * ( 2 * dotRadius + dotSpacing) - dotSpacing

        val startX = (width - totalWidth) / 2 + dotRadius

        for (i in 0 until dotCount) {
            val cx = startX + i * (2 * dotRadius + dotSpacing)
            val cy = height / 2.toFloat()
            canvas.drawCircle(cx, cy, dotRadius, dotPaint)
        }
    }
}