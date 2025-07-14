package com.revoola.utils

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

    fun rl_start(callback: (Long) -> Unit) {
        this.callback = callback
        timer = Timer()
        timerTask = rl_createTimerTask()
        timer?.scheduleAtFixedRate(timerTask, delay, period)
        isPaused = false
    }

    private fun rl_createTimerTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                elapsedTime += period
                callback?.invoke(elapsedTime)
            }
        }
    }

    fun rl_pause() {
        if (!isPaused) {
            timerTask?.cancel()
            timer?.purge()
            isPaused = true
        }
    }

    fun rl_resume() {
        if (isPaused) {
            timer = Timer()
            timerTask = rl_createTimerTask()
            timer?.scheduleAtFixedRate(timerTask, delay, period)
            isPaused = false
        }
    }

    fun rl_stop() {
        timerTask?.cancel()
        timer?.cancel()
        timer?.purge()
        timer = null
        timerTask = null
        elapsedTime = 0
        isPaused = false
    }

    fun rl_getElapsedTime(): Long {
        return elapsedTime
    }
}
