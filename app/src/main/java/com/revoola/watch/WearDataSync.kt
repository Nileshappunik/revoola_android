package com.revoola.watch

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import com.google.gson.Gson
import com.revoola.model.RLWatchModel

class WearDataSync(private val dataClient: DataClient, private val nodeClient: NodeClient) {

    fun sendUserDataToWatch(watchModel: RLWatchModel, callback: (Boolean, String?) -> Unit) {
        // Check if the watch is connected
        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
            if (nodes.isNotEmpty()) {
                // Watch is connected, send data
                val jsonData = Gson().toJson(watchModel) // Convert object to JSON string
                val putDataReq = PutDataMapRequest.create("/sync/userdata").run {
                    dataMap.putString("userdata", jsonData)
                    asPutDataRequest()
                }

                dataClient.putDataItem(putDataReq)
                    .addOnSuccessListener {
                        Log.d("WearDataSync", "User data sent: $jsonData")
                        callback(true, "User data sent successfully") // Success callback
                    }
                    .addOnFailureListener { e ->
                        Log.e("WearDataSync", "Failed to send user data", e)
                        callback(false, "Failed to send user data: ${e.message}") // Failure callback
                    }
            } else {
                // Watch is not connected
                Log.d("WearDataSync", "No connected watch found")
                callback(false, "No connected watch found") // Failure callback
            }
        }.addOnFailureListener { e ->
            Log.e("WearDataSync", "Failed to check connected nodes", e)
            callback(false, "Failed to check connected nodes: ${e.message}") // Failure callback
        }
    }
}

/*class WearDataSync(private val dataClient: DataClient) {

    fun sendUserDataToWatch(watchModel: RLWatchModel) {
        val jsonData = Gson().toJson(watchModel) // Convert object to JSON string
        val putDataReq = PutDataMapRequest.create("/sync/userdata").run {
            dataMap.putString("userdata", jsonData)
            asPutDataRequest()
        }

        dataClient.putDataItem(putDataReq)
            .addOnSuccessListener { Log.d("WearDataSync", "User data sent: $jsonData") }
            .addOnFailureListener { e -> Log.e("WearDataSync", "Failed to send user data", e) }
    }
}*/
