package com.example.myfirstapp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class RLCircularProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0
    private var maxProgress = 100
    private var progressColor = Color.GREEN
    private var backgroundColor = Color.GRAY
    private var strokeWidth = 20f

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = backgroundColor
        strokeWidth = this@RLCircularProgressBar.strokeWidth
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = progressColor
        strokeWidth = this@RLCircularProgressBar.strokeWidth
    }

    init {
        // Optional: Initialize attributes from XML
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (Math.min(width, height) / 2f) - (strokeWidth / 2f)

        // Draw background circle (80% of a full circle)
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            150f,
            245f,
            false,
            backgroundPaint
        )

        // Draw progress circle (up to 80% of a full circle)
        val sweepAngle = (245f * progress / maxProgress).toFloat()
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            150f,
            sweepAngle,
            false,
            progressPaint
        )
    }

    fun RLsetProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun RLsetMaxProgress(maxProgress: Int) {
        this.maxProgress = maxProgress
        invalidate()
    }

    fun RLsetProgressColor(color: Int) {
        progressPaint.color = color
        invalidate()
    }

    override fun setBackgroundColor(color: Int) {
        backgroundPaint.color = color
        invalidate()
    }

    fun RLsetStrokeWidth(width: Float) {
        this.strokeWidth = width
        backgroundPaint.strokeWidth = width
        progressPaint.strokeWidth = width
        invalidate()
    }
}
