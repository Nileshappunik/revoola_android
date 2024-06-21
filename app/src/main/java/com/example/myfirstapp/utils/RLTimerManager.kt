package com.example.myfirstapp.utils

import java.util.Timer
import java.util.TimerTask


class RLTimerManager {
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var isPaused = false
    private var delay: Long = 1000 // 1 second delay
    private var period: Long = 1000 // 1 second period
    private var elapsedTime: Long = 0
    private var callback: ((Long) -> Unit)? = null

    fun RLstart(callback: (Long) -> Unit) {
        this.callback = callback
        timer = Timer()
        timerTask = RLcreateTimerTask()
        timer?.scheduleAtFixedRate(timerTask, delay, period)
        isPaused = false
    }

    private fun RLcreateTimerTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                elapsedTime += period
                callback?.invoke(elapsedTime)
            }
        }
    }

    fun RLpause() {
        if (!isPaused) {
            timerTask?.cancel()
            timer?.purge()
            isPaused = true
        }
    }

    fun RLresume() {
        if (isPaused) {
            timer = Timer()
            timerTask = RLcreateTimerTask()
            timer?.scheduleAtFixedRate(timerTask, delay, period)
            isPaused = false
        }
    }

    fun RLstop() {
        timerTask?.cancel()
        timer?.cancel()
        timer?.purge()
        timer = null
        timerTask = null
        elapsedTime = 0
        isPaused = false
    }

    fun RLgetElapsedTime(): Long {
        return elapsedTime
    }
}
