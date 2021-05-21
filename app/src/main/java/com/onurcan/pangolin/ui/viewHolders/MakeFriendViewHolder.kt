package com.onurcan.pangolin.ui.viewHolders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.onurcan.pangolin.R
import com.squareup.picasso.Picasso

class MakeFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val parent: View= itemView

    private var mImage : ImageView

    init {
        mImage = parent.findViewById(R.id.makeF_profile)
    }

    fun bindMakeF(image:String){
        Picasso.get().load(image).centerCrop().fit().into(mImage)
    }
}