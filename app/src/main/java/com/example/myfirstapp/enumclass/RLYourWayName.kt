package com.example.myfirstapp.enumclass

enum class RLYourWayName { Pilates,Ride,Run,Walk,Workout,Yoga }


fun RLgetWayName(wayname: RLYourWayName): String {
    return when (wayname) {
        RLYourWayName.Pilates -> "Pilates"
        RLYourWayName.Ride -> "Ride"
        RLYourWayName.Run -> "Run"
        RLYourWayName.Walk -> "Walk"
        RLYourWayName.Workout -> "Workout"
        RLYourWayName.Yoga -> "Yoga"
    }
}