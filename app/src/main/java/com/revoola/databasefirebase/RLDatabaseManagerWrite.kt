package com.revoola.databasefirebase

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

class RLDatabaseManagerWrite {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    fun RLLIVEUSERSEMAILWrite(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RLConstants.LIVE).child(RLConstants.LIVEUSERSEMAIL).child(userId).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }
    fun REVOOLAUSEREMAILSWrite(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSEDSTRUCTURE).child(RLConstants.REVOOLAUSEREMAILS).child(userId).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }
    fun REVOOLAUSERFORSEARCHWrite(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSEDSTRUCTURE).child(RLConstants.REVOOLAUSERFORSEARCH).child(userId).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }
    fun REVOOLAUSERSETTINGSWrite(userId:String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(RLConstants.PROPOSEDSTRUCTURE).child(RLConstants.REVOOLAUSERSETTINGS).child(userId).child(RLConstants.BASICDATA).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }

    fun REVOOLADEEPLINKWrite(userId:String) {
        database.child(RLConstants.PROPOSEDSTRUCTURE).child(RLConstants.REVOOLAUSERSETTINGS)
            .child(userId).child(RLConstants.BASICDATA).child("link").setValue("")
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




}

