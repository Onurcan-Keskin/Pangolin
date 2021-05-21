package com.onurcan.pangolin.ui.contracts

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class IChat {
    interface ViewChat {
        fun initMakeFRecycler()
    }

    interface PresenterChat {
        fun onViewsCreate()
        fun setMakeFRecycler(
            context: FragmentActivity,
            fragmentManager: FragmentManager,
            recyclerView: RecyclerView
        )

    }
}