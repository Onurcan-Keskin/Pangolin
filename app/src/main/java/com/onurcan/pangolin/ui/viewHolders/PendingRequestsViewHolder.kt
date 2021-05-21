package com.onurcan.pangolin.ui.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onurcan.pangolin.R
import com.squareup.picasso.Picasso

/**
 * 功能描述
 *
 * @author o00559125
 * @since 2021-02-17
 */
class PendingRequestsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val parent = itemView

    val imageView: ImageView
    private val pName: TextView

    init {
        imageView = parent.findViewById(R.id.request_image)
        pName = parent.findViewById(R.id.main_chatF_sender)
    }

    fun bindView(image: String, name: String) {
        Picasso.get().load(image).centerCrop().fit().into(imageView)
        pName.text = name
    }
}