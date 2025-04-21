package com.revoola.databasefirebase

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.revoola.model.RLRevoolaUsersSettingsModel

class RlFirebaseWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val authManager = RLAuthManager()
        val userId = authManager.RlgetCurrentUser()?.uid?:""
        RLDatabaseManagerRead().RlUserBasicDataRead(userId) { data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                // Firebase Operations
                RLFirebaseManager().readWatchData(userData,applicationContext)
            }
        }


        return Result.success()
    }
}