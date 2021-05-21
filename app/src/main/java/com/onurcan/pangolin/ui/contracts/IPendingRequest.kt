package com.onurcan.pangolin.ui.contracts

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 功能描述
 *
 * @author o00559125
 * @since 2021-02-17
 */
class IPendingRequest {

    interface ViewPendingRequest {
        fun initUI()
    }

    interface PresenterPendingRequest {
        fun onViewCreated()
        fun setPendingRecycler(
            context: FragmentActivity,
            fragmentManager: FragmentManager,
            recyclerView: RecyclerView
        )
    }
}