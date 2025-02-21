package com.revoola.watch

import android.content.Context
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.revoola.commonobject.RLTools
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await



class RLWatchManager  {
    fun openWatchApp(context: Context) {
        val packageName = "com.revoola"

        CoroutineScope(Dispatchers.IO).launch {
            val nodes = Wearable.getNodeClient(context).connectedNodes.await()
            if (nodes.isEmpty()) {
                RLTools.RlLogEPrint("WatchManager", "No Wear OS devices connected!")
                return@launch
            }

            for (node in nodes) {
                RLTools.RlLogDPrint("WatchManager", "Sending message to node: ${node.displayName}")

                val result = Wearable.getMessageClient(context).sendMessage(
                    node.id,
                    "/open_app",
                    packageName.toByteArray()
                ).await()

                RLTools.RlLogDPrint("WatchManager", "Message sent, result: $result")
            }
        }
    }
    suspend fun isWatchConnected(context: Context): Boolean {
        val nodeList: List<Node> = Wearable.getNodeClient(context).connectedNodes.await()
        RLTools.RlLogDPrint("WatchManager", "Connected Nodes: ${nodeList.map { it.displayName }}")
        return nodeList.isNotEmpty()
    }

}