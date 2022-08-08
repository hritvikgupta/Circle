package com.example.mobichat

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class storiesAdapter(val context: Context, val storiesList: ArrayList<Story>):
    RecyclerView.Adapter<storiesAdapter.storiesAdapterViewHolder>() {

    class storiesAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val storyImage = itemView.findViewById<ImageView>(R.id.storyImage)
        val storyCardView = itemView.findViewById<CardView>(R.id.storyImageCardView)
        val storyImageUser = itemView.findViewById<TextView>(R.id.storyImageUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): storiesAdapterViewHolder {
        val view :View = LayoutInflater.from(context).inflate(R.layout.story_item, parent,false)
        return storiesAdapter.storiesAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: storiesAdapterViewHolder, position: Int) {
        val currentUser = storiesList[position]
        holder.storyImageUser.text = currentUser.userName
        if(currentUser.isSeen() == true){
            holder.storyCardView.setCardBackgroundColor(Color.parseColor("#000000"))
            holder.storyImageUser.text = currentUser.userName
        }
        else
        {
            holder.storyCardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

    override fun getItemCount(): Int {
       return  storiesList.size
    }
}


