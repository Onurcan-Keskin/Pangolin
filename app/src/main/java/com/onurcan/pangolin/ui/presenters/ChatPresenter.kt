package com.onurcan.pangolin.ui.presenters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.onurcan.pangolin.R
import com.onurcan.pangolin.appuser.AppUser
import com.onurcan.pangolin.helpers.FragmentHelper
import com.onurcan.pangolin.models.DataClass
import com.onurcan.pangolin.models.FirebaseDBObject
import com.onurcan.pangolin.ui.contracts.IChat
import com.onurcan.pangolin.ui.fragments.SingleUserFragment
import com.onurcan.pangolin.ui.viewHolders.MakeFriendViewHolder

class ChatPresenter constructor(private val contract: IChat.ViewChat) : IChat.PresenterChat {

    override fun onViewsCreate() {
        contract.initMakeFRecycler()
    }

    override fun setMakeFRecycler(
        context: FragmentActivity,
        fragmentManager: FragmentManager,
        recyclerView: RecyclerView
    ) {
        val options = FirebaseRecyclerOptions.Builder<DataClass.MakeFriends>()
            .setQuery(FirebaseDBObject.getRootUserInfo(), DataClass.MakeFriends::class.java)
            .build()

        val adapterFire =
            object :
                FirebaseRecyclerAdapter<DataClass.MakeFriends, MakeFriendViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): MakeFriendViewHolder {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_make_friends_item, parent, false)
                    return MakeFriendViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: MakeFriendViewHolder,
                    position: Int,
                    model: DataClass.MakeFriends
                ) {
                    val lisResUID = getRef(position).key

                    if (lisResUID == AppUser.getUserId()) {
                        holder.itemView.visibility = View.GONE
                        holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                    } else {
                        holder.bindMakeF(model.photoUrl)
                        holder.parent.setOnClickListener {
                            FragmentHelper.startSingleUserFragmentBundle(lisResUID!!,fragmentManager,context)
                        }
                    }
                }
            }
        adapterFire.startListening()
        recyclerView.adapter = adapterFire
    }

}