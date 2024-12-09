package com.revoola.enumclass

import com.revoola.R

enum class RLTypeOfChallenges (val title: String, val image: Int) {
    Steps("STEPS", R.drawable.fd_steps_green),
    Effort("EFFORT", R.drawable.ic_heart),
    Calories("CALORIES", R.drawable.fd_calories_green),
    Distance("DISTANCE", R.drawable.ic_distance),
    Climbed("CLIMBED", R.drawable.ic_climb),
    Duration("DURATION", R.drawable.fd_active_time_green)
}