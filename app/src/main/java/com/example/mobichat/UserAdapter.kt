package com.example.mobichat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class UserAdapter(val context : Context, val UserList : ArrayList<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
        val UserName  = itemview.findViewById<TextView>(R.id.username)
        val UserImage = itemview.findViewById<ImageView>(R.id.userIm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view :View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = UserList[position]
        holder.UserName.text = currentUser.username
        holder.itemView.setOnClickListener{
            val intent = Intent(context, chat_activity::class.java)
            intent.putExtra("name",currentUser.username)
            intent.putExtra("uid", currentUser?.uid)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return UserList.size
    }
}