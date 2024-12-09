package com.revoola.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class RLStatusBarView : FrameLayout {
    constructor(context: Context) : super(context){
        minimumHeight = RLgetStatusBarHeight()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        minimumHeight = RLgetStatusBarHeight()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        minimumHeight = RLgetStatusBarHeight()
    }

    private fun RLgetStatusBarHeight(): Int {
        var result = 0
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

}