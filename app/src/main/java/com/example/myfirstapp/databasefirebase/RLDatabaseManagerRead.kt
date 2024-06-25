package com.example.myfirstapp.databasefirebase

import com.example.myfirstapp.utils.RLConstants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

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
}

/*
private fun readData(path: String) {
    databaseManager.readData(path) { data, error ->
        if (data != null) {
            Log.d("MainActivity", "Data read: $data")
            Toast.makeText(this, "Data read: $data", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Read failed: ${error?.message}", Toast.LENGTH_SHORT).show()
        }
    }
}*/