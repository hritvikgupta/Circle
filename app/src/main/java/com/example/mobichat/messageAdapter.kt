package com.example.mobichat

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MessageAdapter(val context: Context, val messageList:ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val item_receive =1
    val item_sent = 2
    val imageMessagesent = 3
    val imageMessagereceived =4
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sendMessage = itemView.findViewById<TextView>(R.id.sendMessage)

    }
    class SendImageView(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sendImage = itemView.findViewById<ImageView>(R.id.imagesend)
    }
    class ReceiveImageView(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveImage = itemView.findViewById<ImageView>(R.id.imagereceive)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.receivedMessage)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                if (viewType == 1 ) {
                    val view :View = LayoutInflater.from(context).inflate(R.layout.received, parent,false)
                    return ReceiveViewHolder(view)

                }
                else if (viewType==2){
                    val view :View = LayoutInflater.from(context).inflate(R.layout.send, parent,false)
                    return SentViewHolder(view)
                }
                else if(viewType ==3) {
                    val view: View = LayoutInflater.from(context).inflate(R.layout.image_send, parent, false)
                    return SendImageView(view)
                }
                else {
                    val view: View = LayoutInflater.from(context).inflate(R.layout.image_receive, parent, false)
                    return ReceiveImageView(view)
                }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass ==  SentViewHolder::class.java){
            // do stuff sent view holder
            // Below is the type casting in which we are converting holder to sent view holder as we
            // left empty the RecyclerView.Adapter view holder because we have 2 view holder
            val viewHolder = holder as SentViewHolder
            holder.sendMessage.text = currentMessage.message
        }
        else if(holder.javaClass == ReceiveViewHolder::class.java)
        {
            // do stuff for receive view holder
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message

        }
        else if(holder.javaClass == SendImageView::class.java)
        {
            val viewHolder = holder as SendImageView
           // Toast.makeText(context,""+currentMessage.imageMessage,Toast.LENGTH_SHORT).show()
            //holder.sendImage.setImageURI(Uri.parse(currentMessage.imageMessage))
            Picasso
                .get()
                .load(currentMessage.imageMessage)
                .into(holder.sendImage);
        }
        else if(holder.javaClass == ReceiveImageView::class.java)
        {
            val viewHolder = holder as ReceiveImageView
            Picasso
                .get()
                .load(currentMessage.imageMessage)
                .into(holder.receiveImage);
            //holder.receiveImage.setImageURI(Uri.parse(currentMessage.imageMessage))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId))
        {
            if(currentMessage.imageMessage == null)
                return item_sent
            else
                return imageMessagesent
        }
        else {
            if (currentMessage.imageMessage == null)
                return item_receive
            else
                return imageMessagereceived
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}