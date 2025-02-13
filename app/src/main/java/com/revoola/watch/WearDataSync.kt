package com.revoola.watch

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import com.google.gson.Gson
import com.revoola.model.RLWatchModel

class WearDataSync(private val dataClient: DataClient) {

    fun sendUserDataToWatch(watchModel: RLWatchModel) {
        val jsonData = Gson().toJson(watchModel) // Convert object to JSON string
        val putDataReq = PutDataMapRequest.create("/sync/userdata").run {
            dataMap.putString("userData", jsonData)
            asPutDataRequest()
        }

        dataClient.putDataItem(putDataReq)
            .addOnSuccessListener { Log.d("WearDataSync", "User data sent: $jsonData") }
            .addOnFailureListener { e -> Log.e("WearDataSync", "Failed to send user data", e) }
    }
}
