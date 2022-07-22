package com.example.mobichat

import android.net.Uri

class Message
{
    var message: String? = null
    var senderId: String? = null
    var imageMessage:String? = null

    constructor(){}

    constructor(message:String?=null, senderId: String?=null, imageMessage: String? = null)
    {
        this.message = message
        this.senderId = senderId
        this.imageMessage = imageMessage
    }


}