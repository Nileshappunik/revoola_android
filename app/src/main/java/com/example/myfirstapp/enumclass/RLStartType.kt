package com.example.myfirstapp.enumclass

import com.example.myfirstapp.R

val img_menu_yourWay = "https://video.revoola.com/v3/menu/start/your_way.png"
val img_menu_body = "https://video.revoola.com/v3/menu/start/body_classes.png"
val img_menu_mind = "https://video.revoola.com/v3/menu/start/mind_classes.png"
val img_menu_challenge = "https://video.revoola.com/v3/menu/start/challenges.png"

val ic_menu_yourWay = "https://video.revoola.com/v3/icons/menu/your_way.svg"
val ic_menu_body = "https://video.revoola.com/v3/icons/menu/body_classes.svg"
val ic_menu_mind = "https://video.revoola.com/v3/icons/menu/mind_classes.svg"
val ic_menu_challenge = "https://video.revoola.com/v3/icons/menu/challenges.svg"


val ic_yourWay_walk = "https://video.revoola.com/v3/icons/menu/your_way/walk.svg"
val ic_yourWay_run = "https://video.revoola.com/v3/icons/menu/your_way/run.svg"
val ic_yourWay_ride = "https://video.revoola.com/v3/icons/menu/your_way/ride.svg"
val ic_yourWay_workout = "https://video.revoola.com/v3/icons/menu/your_way/workout.svg"

val img_yourWay_walk = "https://video.revoola.com/v3/menu/your_way/walk.png"
val img_yourWay_run = "https://video.revoola.com/v3/menu/your_way/run.png"
val img_yourWay_ride = "https://video.revoola.com/v3/menu/your_way/ride.png"
val img_yourWay_workout = "https://video.revoola.com/v3/menu/your_way/workout.png"

val ic_challenges_steps = "https://video.revoola.com/v3/icons/menu/challenges/steps.svg"
val ic_challenges_effort = "https://video.revoola.com/v3/icons/menu/challenges/effort.svg"
val ic_challenges_calories = "https://video.revoola.com/v3/icons/menu/challenges/calories.svg"
val ic_challenges_distance = "https://video.revoola.com/v3/icons/menu/challenges/distance.svg"
val ic_challenges_climbed = "https://video.revoola.com/v3/icons/menu/challenges/climbed.svg"
val ic_challenges_duration = "https://video.revoola.com/v3/icons/menu/challenges/duration.svg"

val img_challenges_steps = "https://video.revoola.com/v3/menu/challenges/steps.png"
val img_challenges_effort = "https://video.revoola.com/v3/menu/challenges/effort.png"
val img_challenges_calories = "https://video.revoola.com/v3/menu/challenges/calories.png"
val img_challenges_distance = "https://video.revoola.com/v3/menu/challenges/distance.png"
val img_challenges_climbed = "https://video.revoola.com/v3/menu/challenges/climbed.png"
val img_challenges_duration = "https://video.revoola.com/v3/menu/challenges/duration.png"

val ic_friend_find = "https://video.revoola.com/v3/icons/menu/your_way.svg"
val ic_friend_friends = "https://video.revoola.com/v3/icons/menu/body_classes.svg"
val ic_friend_group = "https://video.revoola.com/v3/icons/menu/mind_classes.svg"
val ic_friend_invite = "https://video.revoola.com/v3/icons/menu/challenges.svg"

val img_friend_find = "https://video.revoola.com/v3/menu/your_way/walk.png"
val img_friend_friends = "https://video.revoola.com/v3/menu/your_way/run.png"
val img_friend_group = "https://video.revoola.com/v3/menu/your_way/ride.png"
val img_friend_invite = "https://video.revoola.com/v3/menu/your_way/workout.png"
enum class RLStartType (val title: String, val icon_image: String, val description:Int,val image:String) {
    MindClasses("MIND CLASSES", ic_menu_mind,R.string.mindclassdescription,img_menu_mind),
    BodyClasses("BODY CLASSES", ic_menu_body,R.string.bodyclassdescription,img_menu_body),
    YourWay("YOUR WAY",ic_menu_yourWay,R.string.yourwaydescription,img_menu_yourWay),
    Challenges("CHALLENGES", ic_menu_challenge,R.string.challengesdescription,img_menu_challenge),
    Walk("WALK", ic_yourWay_walk,R.string.challengesdescription,img_yourWay_walk),
    Run("RUN", ic_yourWay_run,R.string.challengesdescription,img_yourWay_run),
    Ride("RIDE", ic_yourWay_ride,R.string.challengesdescription,img_yourWay_ride),
    Workout("WORKOUT", ic_yourWay_workout,R.string.challengesdescription,img_yourWay_workout),
    Steps("STEPS", ic_challenges_steps,R.string.mindclassdescription,img_challenges_steps),
    Effort("EFFORT",  ic_challenges_effort,R.string.mindclassdescription,img_challenges_effort),
    Calories("CALORIES",  ic_challenges_calories,R.string.mindclassdescription,img_challenges_calories),
    Distance("DISTANCE", ic_challenges_distance,R.string.mindclassdescription,img_challenges_distance),
    Climbed("CLIMBED", ic_challenges_climbed,R.string.mindclassdescription,img_challenges_climbed),
    Duration("DURATION", ic_challenges_duration,R.string.mindclassdescription,img_challenges_duration),
    FindOnRevoola("FIND ON REVOOLA", ic_friend_find,R.string.mindclassdescription,img_friend_find),
    YourFriend("YOUR FRIENDS", ic_friend_friends,R.string.mindclassdescription,img_friend_friends),
    YourGroup("YOUR GROUPS", ic_friend_group,R.string.mindclassdescription,img_friend_group),
    InviteToJoin("INVITE TO JOIN", ic_friend_invite,R.string.mindclassdescription,img_friend_invite)
}

