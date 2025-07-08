package com.revoola.enumclass

enum class RLChallengeStatusType(val value: Int) {
    Pending(0),
    Accepted(1),
    Completed(2),
    Declined(3),
    Missed(4),
    OwnerWinner(5),
    OtherWinner(6),
    Ignored(7);

    companion object {
        fun from(value: Int) = values().firstOrNull { it.value == value } ?: Pending
    }
}