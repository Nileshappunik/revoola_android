package com.revoola.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class RLMyPushService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        // Handle the push message here (custom actions)
        super.onMessageReceived(message)
        //PushManager.handlePushPayload(applicationContext, message.data)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle the new token, e.g., send it to your server
       // PushManager.getInstance(applicationContext).refreshToken(applicationContext, token)
    }
}