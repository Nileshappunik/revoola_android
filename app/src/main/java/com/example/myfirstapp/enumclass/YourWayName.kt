package com.example.myfirstapp.enumclass

enum class YourWayName { Pilates,Ride,Run,Walk,Workout,Yoga }


fun getWayName(wayname: YourWayName): String {
    return when (wayname) {
        YourWayName.Pilates -> "Pilates"
        YourWayName.Ride -> "Ride"
        YourWayName.Run -> "Run"
        YourWayName.Walk -> "Walk"
        YourWayName.Workout -> "Workout"
        YourWayName.Yoga -> "Yoga"
    }
}