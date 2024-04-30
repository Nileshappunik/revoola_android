package com.example.myfirstapp.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class StatusBarView : FrameLayout {
    constructor(context: Context) : super(context){
        minimumHeight = getStatusBarHeight()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        minimumHeight = getStatusBarHeight()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        minimumHeight = getStatusBarHeight()
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

}