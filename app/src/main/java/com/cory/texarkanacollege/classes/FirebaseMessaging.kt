package com.cory.texarkanacollege.classes

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessaging : FirebaseMessagingService() {

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.d("Deleted", "Deleted Message")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d("Message", "From: ${p0.from}")

        if (p0.data.isNotEmpty()) {
            Log.d("Message", "Message data payload: ${p0.data}")

        }
        p0.notification?.let {
            Log.d("Message", "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("Token", "Refreshed token: $p0")

        sendRegistrationToServer(p0)
    }

    private fun sendRegistrationToServer(p0: String?) {
        Log.d("token", "sendRegistrationTokenToServer($p0)")
    }
}