package com.revoola.databasefirebase

import com.revoola.utils.RLConstants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RLDatabaseManagerWrite {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    fun RLLIVEUSERSEMAILWrite(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RLConstants.LIVE).child(RLConstants.LIVE_USERS_EMAIL).child(userId).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }
    fun REVOOLAUSEREMAILSWrite(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSED_STRUCTURE).child(RLConstants.REVOOLA_USER_EMAILS).child(userId).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }
    fun REVOOLAUSERFORSEARCHWrite(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSED_STRUCTURE).child(RLConstants.REVOOLA_USER_FOR_SEARCH).child(userId).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }
    fun REVOOLAUSERSETTINGSWrite(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSED_STRUCTURE).child(RLConstants.REVOOLA_USER_SETTINGS).child(userId).child(RLConstants.BASIC_DATA).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }

    fun REVOOLADEEPLINKWrite(userId:String) {
        database.child(RLConstants.PROPOSED_STRUCTURE).child(RLConstants.REVOOLA_USER_SETTINGS)
            .child(userId).child(RLConstants.BASIC_DATA).child("link").setValue("")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //success
                } else {
                    //fail
                }
            }
    }

    fun RlWriteData(path: String, data: Any, callback: (Boolean, Exception?) -> Unit) {
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

    fun RlGuestUpdateUserWrite(path: String, data: Any, callback: (Boolean, Exception?) -> Unit) {
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

