package com.revoola.enumclass

enum class FriendsAPIStatusType(val value: String) {
    Follow("-1"),
    Invite("0"),
    Invited("1"),
    Requested("2"),
    Accepted("3"),
    Blocked("4");

    companion object {
        fun fromValue(value: String): FriendsAPIStatusType? {
            return values().find { it.value == value }
        }
    }
}
