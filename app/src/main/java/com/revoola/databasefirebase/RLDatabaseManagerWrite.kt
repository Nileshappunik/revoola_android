package com.revoola.databasefirebase

import com.revoola.utils.RLConstants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RLDatabaseManagerWrite {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val d2_database = FirebaseDatabase.getInstance("https://rideathome-9080e-252d2.firebaseio.com/").reference

    fun rl_Live_users_Email_Write(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RLConstants.LIVE).child(RLConstants.LIVE_USERS_EMAIL).child(userId).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }

    fun revoola_User_Emails_write(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RevoolaFirebasePath.basePath).child(RLConstants.REVOOLA_USER_EMAILS).child(userId).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }
    fun revoola_User_For_Search_Write(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RevoolaFirebasePath.basePath).child(RLConstants.REVOOLA_USER_FOR_SEARCH).child(userId).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }
    fun revoola_User_Settings_Write(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RevoolaFirebasePath.basePath).child(RLConstants.REVOOLA_USER_SETTINGS).child(userId).child(RLConstants.BASIC_DATA).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }

    fun revoola_Deep_Link_Write(userId:String) {
        database.child(RevoolaFirebasePath.basePath).child(RLConstants.REVOOLA_USER_SETTINGS)
            .child(userId).child(RLConstants.BASIC_DATA).child("link").setValue("")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //success
                } else {
                    //fail
                }
            }
    }

    fun rl_write_Basic_Data_Update(path: String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(path).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }

    fun rl_update_All_Data(path: String, data: Any) {
        database.child(path).setValue(data)
    }

    fun rl_write_Data(path: String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        val entryIdSummery = (System.currentTimeMillis() / 1000).toString()
        entryIdSummery.let {
            database.child(path).child(it).setValue(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, null)
                    } else {
                        callback(false, task.exception)
                    }
                }
        }
    }

    fun rl_write_Data_For_Testing_Data(path: String, dataMap: Map<String,Any>, callback: (Boolean, Exception?) -> Unit) {
        dataMap.forEach { (category, entry) ->
            d2_database.child(path).child(category).updateChildren(entry as Map<String, Any>)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, null)
                    } else {
                        callback(false, task.exception)
                    }
                }
        }
    }


    fun rl_guest_Update_User_Write(path: String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        val entryIdSummery = (System.currentTimeMillis() / 1000).toString()
        entryIdSummery.let {
            database.child(path).child(it).setValue(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, null)
                    } else {
                        callback(false, task.exception)
                    }
                }
        }
    }




}

