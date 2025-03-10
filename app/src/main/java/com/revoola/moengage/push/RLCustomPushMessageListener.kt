package com.revoola.moengage.push

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.moengage.pushbase.push.PushMessageListener
import com.revoola.commonobject.RLTools


class RLCustomPushMessageListener: PushMessageListener() {

    override fun onNotificationReceived(context: Context, payload: Bundle) {
        super.onNotificationReceived(context, payload)
       RLTools.RlLogEPrint("MoengageTag","RLCustomPushMessageListener : Notification received $payload")

    }

    override fun onNotificationCleared(context: Context, payload: Bundle) {
        super.onNotificationCleared(context, payload)
       RLTools.RlLogEPrint("MoengageTag","RLCustomPushMessageListener : Notification Cleared $payload")
    }

    override fun onNotificationClick(activity: Activity, payload: Bundle): Boolean {
        super.onNotificationClick(activity, payload)
       RLTools.RlLogEPrint("MoengageTag","RLCustomPushMessageListener : Notification clicked $payload")
        return false
    }

    override fun handleCustomAction(context: Context, payload: String) {
        super.handleCustomAction(context, payload)
       RLTools.RlLogEPrint("MoengageTag","RLCustomPushMessageListener : Callback for custom action.")
    }
}