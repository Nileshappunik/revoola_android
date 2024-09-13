package com.example.myfirstapp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class RLNonScrollableRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        // Don't intercept touch events if scroll is disabled
        return if (isScrollable()) {
            super.onInterceptTouchEvent(e)
        } else {
            false
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // Handle touch events only if scroll is enabled
        return if (isScrollable()) {
            super.onTouchEvent(e)
        } else {
            e.action == MotionEvent.ACTION_UP && performClick()
        }
    }

    private fun isScrollable(): Boolean {
        // Return false to disable scrolling
        return false
    }
}
