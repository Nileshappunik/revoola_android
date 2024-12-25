package com.revoola.services

import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth

class ClassFilterService(
    private val firebaseDatabase: FirebaseDatabase,
    private val auth: FirebaseAuth
)
{

    private val instructorList = mutableListOf<String>()
    private val difficultyList = mutableListOf<String>()
    private val classTypeList = mutableListOf<String>()
    private val sessionTypeList = mutableListOf<String>()
    private val durationList = mutableListOf<String>()
    private val takenByMeList = mutableListOf<String>()
    private val challengeList = mutableListOf<String>()
    private val scheduleList = mutableListOf<String>()
    private val favoriteList = mutableListOf<VideoItem>()
    private val takenByMe = mutableListOf<VideoItem>()
    private val challengeItems = mutableListOf<VideoItem>()

    // Method to fetch the class taken by the user
    fun fetchClassTakenByMe() {
        val userId = auth.currentUser?.uid ?: return
        val ref = firebaseDatabase.getReference("userlist/$userId/revoolaUserCompletedVideos")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    takenByMe.add(VideoItem(it.key ?: "", true))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Fetch the user's favorite classes
    fun fetchMyFavorite() {
        val userId = auth.currentUser?.uid ?: return
        val ref = firebaseDatabase.getReference("userlist/$userId/bookmarked")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favoriteList.clear()
                snapshot.children.forEach {
                    val videoItem = VideoItem(it.key ?: "", true)
                    favoriteList.add(videoItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Insert a class taken by the user
    fun insertClassTakenByMe(videoKey: String) {
        val userId = auth.currentUser?.uid ?: return
        val ref = firebaseDatabase.getReference("userlist/$userId/revoolaUserCompletedVideos/$videoKey")
        ref.setValue(true)
    }

    // Method to handle class filtering based on selected criteria
    fun select(name: String, data: FilterItem) {
        when (name) {
            "Instructor" -> modifyFilter(instructorList, data)
            "Difficulty" -> modifyFilter(difficultyList, data)
            "Activity" -> modifyFilter(classTypeList, data)
            "Class Type" -> modifyFilter(sessionTypeList, data)
            "Duration" -> modifyFilter(durationList, data)
            "Taken By Me" -> modifyFilter(takenByMeList, data)
            "Challenge" -> modifyFilter(challengeList, data)
            "Schedule" -> modifyFilter(scheduleList, data)
        }
    }

    // Modify filter list based on the clicked option
    private fun modifyFilter(list: MutableList<String>, data: FilterItem) {
        val index = list.indexOf(data.key)
        if (index != -1) {
            list[index] = data.key
        } else {
            list.add(data.key)
        }
    }

    // Convert array object to list
    private fun convertToArrayKey(obj: Map<String, Boolean>): List<FilterItem> {
        return obj.keys.map { FilterItem(it, false) }
    }

    // Convert array to list of video items
    private fun convertToArray(obj: Map<String, VideoItem>): List<VideoItem> {
        return obj.values.toList()
    }

    // Additional methods to handle filtering logic
    fun getTotalClassByFilter(): List<VideoItem> {
        return takenByMe.filter { video ->
            classTypeList.contains(video.classType) &&
                    difficultyList.contains(video.difficulty) &&
                    instructorList.contains(video.instructor)
        }
    }
}

data class FilterItem(val key: String, val status: Boolean)

data class VideoItem(
    val key: String,
    val status: Boolean,
    val classType: String = "",
    val difficulty: String = "",
    val instructor: String = ""
)







