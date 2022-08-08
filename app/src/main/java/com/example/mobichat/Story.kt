package com.example.mobichat

import android.widget.ImageView

class Story {

    var seen:Boolean? = null
    var image: ImageView?=null
    var userName:String?=null

    constructor(){}

    constructor(seen:Boolean, image: ImageView? = null, userName:String?=null)
    {
       this.seen = seen
       this.userName = userName
    }

    public fun isSeen(): Boolean? {
        return seen
    }
    public fun setSeen(seen: Boolean){
        this.seen = seen
    }

}