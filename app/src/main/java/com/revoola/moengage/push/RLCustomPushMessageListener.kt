package com.revoola.moengage.push

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.moengage.pushbase.push.PushMessageListener



class RLCustomPushMessageListener: PushMessageListener() {

    override fun onNotificationReceived(context: Context, payload: Bundle) {
        super.onNotificationReceived(context, payload)
        Log.e("RLCustomPushMessageListener","Notification received $payload")

    }

    override fun onNotificationCleared(context: Context, payload: Bundle) {
        super.onNotificationCleared(context, payload)
        Log.e("RLCustomPushMessageListener","Notification Cleared $payload")
    }

    override fun onNotificationClick(activity: Activity, payload: Bundle): Boolean {
        super.onNotificationClick(activity, payload)
        Log.e("RLCustomPushMessageListener","Notification clicked $payload")
        return false
    }

    override fun handleCustomAction(context: Context, payload: String) {
        super.handleCustomAction(context, payload)
        Log.e("RLCustomPushMessageListener","Callback for custom action.")
    }
}