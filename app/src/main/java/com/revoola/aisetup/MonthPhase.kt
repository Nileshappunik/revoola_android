package com.revoola.aisetup

import java.time.LocalDate

enum class MonthPhase { START, MID, END;

    companion object {
        fun current(): MonthPhase {
            val day = LocalDate.now().dayOfMonth
            return when (day) {
                in 1..10 -> START
                in 11..20 -> MID
                else -> END
            }
        }
    }
}