package com.cory.texarkanacollege.classes

import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener
import com.google.firebase.inappmessaging.model.Action
import com.google.firebase.inappmessaging.model.InAppMessage

class InAppMessagingClickListener : FirebaseInAppMessagingClickListener {
    override fun messageClicked(p0: InAppMessage, p1: Action) {
        println("in app message " + p0.toString() + " " + p1.toString())
    }
}