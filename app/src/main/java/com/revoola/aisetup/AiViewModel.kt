package com.revoola.aisetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AiViewModel : ViewModel() {

    private val _state = MutableStateFlow(AiUiState(isConfigured = RELOpenAIManager.isConfigured()))
    val state: StateFlow<AiUiState> = _state

    // Separate method to load motivation message
    fun loadMotivationMessage(stats: MonthlyStats) {
        viewModelScope.launch {
            if (!RELOpenAIManager.isConfigured()) {
                _state.value = _state.value.copy(error = "GROQ key not configured.")
                return@launch
            }

            val motivation = RELOpenAIManager.generateMotivationMessage(stats)
            _state.value = _state.value.copy(motivation = motivation)
        }
    }

    // Separate method to load weight loss forecast
    fun loadWeightLossForecast(stats: MonthlyStats) {
        viewModelScope.launch {
            if (!RELOpenAIManager.isConfigured()) {
                _state.value = _state.value.copy(error = "GROQ key not configured.")
                return@launch
            }

            val forecast = RELOpenAIManager.generateWeightLossForecast(stats)
            _state.value = _state.value.copy(forecast = forecast)
        }
    }

    // Separate method to load workout notification (title and message)
    fun loadWorkoutNotification(stats: MonthlyStats, day: Int) {
        viewModelScope.launch {
            if (!RELOpenAIManager.isConfigured()) {
                _state.value = _state.value.copy(error = "GROQ key not configured.")
                return@launch
            }

            val (title, message) = RELOpenAIManager.generateWorkoutNotification(stats, day)
            _state.value = _state.value.copy(notifTitle = title, notifMessage = message)
        }
    }

    // Separate method to load next workout notification message
    fun loadNextWorkoutNotificationMessage(stats: MonthlyStats, day: Int) {
        viewModelScope.launch {
            if (!RELOpenAIManager.isConfigured()) {
                _state.value = _state.value.copy(error = "GROQ key not configured.")
                return@launch
            }

            val nextNotif = RELOpenAIManager.generateNextWorkoutNotificationMessage(stats, day)
            _state.value = _state.value.copy(nextNotif = nextNotif)
        }
    }
}


//class AiViewModel : ViewModel() {
//
//    private val _state = MutableStateFlow(AiUiState(isConfigured = RELOpenAIManager.isConfigured()))
//    val state: StateFlow<AiUiState> = _state
//
//    fun load(stats: MonthlyStats, day: Int) {
//        viewModelScope.launch {
//            if (!RELOpenAIManager.isConfigured()) {
//                _state.value = _state.value.copy(error = "GROQ key not configured.")
//                return@launch
//            }
//
//            val motivation = RELOpenAIManager.generateMotivationMessage(stats)
//            val forecast = RELOpenAIManager.generateWeightLossForecast(stats)
//            val (title, message) = RELOpenAIManager.generateWorkoutNotification(stats, day)
//            val next = RELOpenAIManager.generateNextWorkoutNotificationMessage(stats, day)
//
//            _state.value = _state.value.copy(
//                motivation = motivation,
//                forecast = forecast,
//                notifTitle = title,
//                notifMessage = message,
//                nextNotif = next,
//                error = null
//            )
//        }
//    }
//}