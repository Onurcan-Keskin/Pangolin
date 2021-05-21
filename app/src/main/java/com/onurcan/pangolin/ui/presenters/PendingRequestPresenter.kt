package com.onurcan.pangolin.ui.presenters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.onurcan.exovideoreference.utils.showLogDebug
import com.onurcan.pangolin.R
import com.onurcan.pangolin.appuser.AppUser
import com.onurcan.pangolin.helpers.FragmentHelper
import com.onurcan.pangolin.models.DataClass
import com.onurcan.pangolin.models.FirebaseDBObject
import com.onurcan.pangolin.ui.contracts.IPendingRequest
import com.onurcan.pangolin.ui.viewHolders.PendingRequestsViewHolder

/**
 * 功能描述
 *
 * @author o00559125
 * @since 2021-02-17
 */
class PendingRequestPresenter constructor(private val contract: IPendingRequest.ViewPendingRequest) :
    IPendingRequest.PresenterPendingRequest {
    override fun onViewCreated() {
        contract.initUI()
    }

    override fun setPendingRecycler(
        context: FragmentActivity,
        fragmentManager: FragmentManager,
        recyclerView: RecyclerView
    ) {
        val options = FirebaseRecyclerOptions.Builder<DataClass.PendingRequests>()
            .setQuery(
                FirebaseDBObject.getReceivedFriendRequests(AppUser.getUserId()),
                DataClass.PendingRequests::class.java
            )
            .build()

        val adapterFire = object :
            FirebaseRecyclerAdapter<DataClass.PendingRequests, PendingRequestsViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): PendingRequestsViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.single_pending_request_item, parent, false)
                return PendingRequestsViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: PendingRequestsViewHolder,
                position: Int,
                model: DataClass.PendingRequests
            ) {
                val lisResUID = getRef(position).key
                holder.bindView(model.photo,model.name)

                holder.imageView.setOnClickListener {
                    FragmentHelper.startSingleUserFragmentBundle(lisResUID!!,fragmentManager,context)
                }
                showLogDebug(this.javaClass.simpleName,"name ${model.name} photo ${model.photo}")
            }
        }
        adapterFire.startListening()
        recyclerView.adapter=adapterFire
    }

}