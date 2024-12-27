package com.revoola.databasefirebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.revoola.utils.RLConstants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.revoola.commonobject.RLTools
import com.revoola.firebaseModel.RLChallengeRiderBody

class RLDatabaseManagerRead {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun RLREVOOLAUSERFORSEARCHREADDATE(userId: String, callback: (Any?, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSEDSTRUCTURE).child(RLConstants.REVOOLAUSERFORSEARCH).child(userId)
            .get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }
    fun RLREVOOLAVIDEOKEYSMINDRead(classname: String, callback: (Any?, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSEDSTRUCTURE).child(RLConstants.REVOOLAVIDEOKEYSMIND)
            .child(classname).child(RLConstants.LISTOFVIDEOS)
            .get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }

    fun RLALLMENULISTRead(classname: String, callback: (Any?, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSEDSTRUCTURE).child(RLConstants.CODESECTION)
            .child(RLConstants.AVAILABLEMENUS).child(classname)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result?.value, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }
    fun RLRevoolaVideoKeysRead(classname: String, callback: (Any?, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSEDSTRUCTURE).child(RLConstants.REVOOLAVIDEOKEYS)
            .child(classname).child(RLConstants.LISTOFVIDEOS)
            .get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }

    fun RLRevoolaVideosRead(videoId:String,callback: (Any?, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSEDSTRUCTURE).child(RLConstants.REVOOLAVIDEOS).child(videoId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result?.value, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }
    fun RLRevoolaVideosMindRead(videoId:String,callback: (Any?, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSEDSTRUCTURE).child(RLConstants.REVOOLAVIDEOSMIND).child(videoId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result?.value, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }
    fun RlreadData(path: String, callback: (Any?, Exception?) -> Unit) {
        database.child(path).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }

    fun RlUserBasicDataRead(userId: String,callback: (Any?, Exception?) -> Unit) {
        val path ="/proposedstructure/revoolaUserSettings/$userId/basicData"
        database.child(path).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.value, null)
            } else {
                callback(null, task.exception)
            }
        }
    }

    fun RLClassLeaderBoardDataRead(viedoId: String,callback: (Any?, String?) -> Unit){
        val leaderboardMap = mutableMapOf<String, RLChallengeRiderBody>()
        val path ="/proposedstructure/revoolaClassLeaderBoards/$viedoId"
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


/*
private fun readData(path: String) {
    databaseManager.readData(path) { data, error ->
        if (data != null) {
            RLTools.RlLogDPrint("MainActivity", "Data read: $data")
            Toast.makeText(this, "Data read: $data", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Read failed: ${error?.message}", Toast.LENGTH_SHORT).show()
        }
    }
}*/