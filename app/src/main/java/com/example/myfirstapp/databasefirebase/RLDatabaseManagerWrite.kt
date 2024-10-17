package com.example.myfirstapp.databasefirebase

import android.util.Log
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.fragment.overview.RLFragOverviewSession
import com.example.myfirstapp.model.RLHeartRateSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.utils.RLConstants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

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
    fun RlWriteData(path: String, data: Any, callback: (Boolean, Exception?) -> Unit) {
        database.child(path).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception)
                }
            }
    }

}

