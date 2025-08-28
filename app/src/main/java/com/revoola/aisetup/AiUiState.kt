package com.revoola.aisetup

data class AiUiState(
    val isConfigured: Boolean = false,
    val phase: MonthPhase = MonthPhase.current(),
    val motivation: String? = null,
    val forecast: String? = null,
    val notifTitle: String? = null,
    val notifMessage: String? = null,
    val nextNotif: String? = null,
    val error: String? = null
)

