package com.revoola.services

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.revoola.R

class RLSwipeGestureDetector(val context: Context, val frameLeft:FrameLayout, val frameRight:FrameLayout)  {
    //Animation set left Right Swipe
    public inner class SwipeGestureListenerLeft : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null || e2 == null) return false
            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y
            return if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight(true)
                    } else {
                        onSwipeLeft(true)
                    }
                    return   true
                } else {
                    return  false
                }
            } else {
                return  false
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }

    }
    public inner class SwipeGestureListenerRight : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null || e2 == null) return false
            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y
            return if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight(false)
                    } else {
                        onSwipeLeft(false)
                    }
                    return   true
                } else {
                    return  false
                }
            } else {
                return  false
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }

    }
    private fun onSwipeRight(isLeftSideClick:Boolean) {
        if (isLeftSideClick){
            toggleVisibilityleft(true)
        }else{
            toggleVisibilityright(false)
        }
    }
    private fun onSwipeLeft(isLeftSideClick:Boolean) {
        if (isLeftSideClick){
            toggleVisibilityleft(false)
        }else{
            toggleVisibilityright(true)
        }
    }
    private fun toggleVisibilityleft(visible: Boolean) {
        val anim: Animation = if (visible) {
            AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
        } else {
            AnimationUtils.loadAnimation(context, R.anim.slide_out_left)
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                frameLeft.visibility = if (visible) View.VISIBLE else View.GONE
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        frameLeft.startAnimation(anim)
    }
    private fun toggleVisibilityright(visible: Boolean) {
        val anim: Animation = if (visible) {
            AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
        } else {
            AnimationUtils.loadAnimation(context, R.anim.slide_out_right)
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                frameRight.visibility = if (visible) View.VISIBLE else View.GONE
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        frameRight.startAnimation(anim)
    }


}
