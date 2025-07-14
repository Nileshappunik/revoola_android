package com.revoola.databasefirebase

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.revoola.commonobject.RLTools

class RlFirebaseWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val authManager = RLAuthManager()
        val userId = authManager.rl_getCurrentUser()?.uid?:""
        RLDatabaseManagerRead().rl_userBasicDataRead(userId) { data, error ->
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