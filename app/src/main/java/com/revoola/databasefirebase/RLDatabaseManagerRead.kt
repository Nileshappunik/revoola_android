package com.revoola.databasefirebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.revoola.utils.RLConstants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.revoola.firebaseModel.RLChallengeRiderBody

class RLDatabaseManagerRead {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val d2_database = FirebaseDatabase.getInstance("https://rideathome-9080e-252d2.firebaseio.com/").reference

    fun deleteFirebaseData(path: String,callback: (Any?, Exception?) -> Unit) {
        database.child(path).removeValue()
            .addOnSuccessListener {
                callback("Data deleted successfully from $path", null)
            }
            .addOnFailureListener {
                callback(null, it)
            }
    }


    fun rl_revoolaUserForSearchReaddata(userId: String, callback: (Any?, Exception?) -> Unit) {
        database.child(RevoolaFirebasePath.basePath).child(RLConstants.REVOOLA_USER_FOR_SEARCH).child(userId)
            .get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }
    fun rl_revoolaVideoKeysMindRead(classname: String, callback: (Any?, Exception?) -> Unit) {
        database.child(RevoolaFirebasePath.basePath).child(RLConstants.REVOOLA_VIDEO_KEYS_MIND)
            .child(classname).child(RLConstants.LISTOFVIDEOS)
            .get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }

    fun rl_allMenuListRead(classname: String, callback: (Any?, Exception?) -> Unit) {
        database.child(RevoolaFirebasePath.basePath).child(RLConstants.CODE_SECTION)
            .child(RLConstants.AVAILABLE_MENUS).child(classname)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result?.value, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }
    fun rl_revoolaVideoKeysRead(classname: String, callback: (Any?, Exception?) -> Unit) {
        database.child(RevoolaFirebasePath.basePath).child(RLConstants.REVOOLA_VIDEO_KEYS)
            .child(classname).child(RLConstants.LISTOFVIDEOS)
            .get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }

    fun rl_revoolaVideosRead(videoId:String, callback: (Any?, Exception?) -> Unit) {
        database.child(RevoolaFirebasePath.basePath).child(RLConstants.REVOOLA_VIDEOS).child(videoId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result?.value, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }
    fun rl_revoolaVideosMindRead(videoId:String, callback: (Any?, Exception?) -> Unit) {
        database.child(RevoolaFirebasePath.basePath).child(RLConstants.REVOOLA_VIDEOS_MIND).child(videoId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result?.value, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }
    fun rl_readData(path: String, callback: (Any?, Exception?) -> Unit) {
        database.child(path).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }
    fun rl_d2DataBaseReadData(path: String, callback: (Any?, Exception?) -> Unit) {
        d2_database.child(path).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }

    fun rl_userBasicDataRead(userId: String, callback: (Any?, Exception?) -> Unit) {
        val path = RevoolaFirebasePath.basicDataDataPath(userId)
        database.child(path).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }

    fun rl_appUnitRead(callback: (Any?, Exception?) -> Unit) {
        val path = RevoolaFirebasePath.basicDataPathWrite("appUnit")
        database.child(path).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }

    fun rl_helpVideoGetDataRead(HelpType: String, callback: (Any?, Exception?) -> Unit) {
        val path =RevoolaFirebasePath.getStartedVideosDataPath(HelpType)
        database.child(path).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }

    fun rl_classLeaderBoardDataRead(viedoId: String, callback: (Any?, String?) -> Unit){
        val leaderboardMap = mutableMapOf<String, RLChallengeRiderBody>()
        val path = RevoolaFirebasePath.classLeaderBoardsDataPath(viedoId)
        database.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("FirebaseError", "Fetch snapshot: ${snapshot}")
                for (childSnapshot in snapshot.children) {
                    val key = childSnapshot.key
                    val leaderboardResponse = childSnapshot.getValue(RLChallengeRiderBody::class.java)
                    if (key != null && leaderboardResponse != null) {
                        // Add to the map
                        leaderboardMap[key] = leaderboardResponse
                    }
                }
                callback(leaderboardMap, null)
               /* leaderboardMap.forEach { (key, value) ->
                    Log.e("FirebaseError", "Fetch:-  Key: $key, Display Name: ${value.displayName}, Location: ${value.location}")
                }*/

            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error.message)
            }
        })

    }

}

