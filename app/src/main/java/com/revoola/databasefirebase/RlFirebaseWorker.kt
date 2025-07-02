package com.revoola.databasefirebase

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.revoola.commonobject.RLTools
import com.revoola.model.BooleanDeserializer
import com.revoola.model.DoubleDeserializer
import com.revoola.model.FloatDeserializer
import com.revoola.model.IntDeserializer
import com.revoola.model.LongDeserializer
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.model.StringDeserializer

class RlFirebaseWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val authManager = RLAuthManager()
        val userId = authManager.RlgetCurrentUser()?.uid?:""
        RLDatabaseManagerRead().RlUserBasicDataRead(userId) { data, error ->
            if (data != null) {
               // val gson = Gson()
//                val gson = GsonBuilder()
//                    .registerTypeAdapter(Long::class.java, LongDeserializer())
//                    .create()
               // val jsonObject = gson.toJson(data)
               // val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                val userData = RLTools.parseUserData(data)
                if (userData!=null){
                    RLFirebaseManager().readWatchData(userData,applicationContext)
                }
                // Firebase Operations

            }
        }

        return Result.success()
    }


}