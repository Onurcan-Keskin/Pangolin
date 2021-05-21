package com.onurcan.pangolin.ui.contracts

class ISingleUser {

    interface ViewSingleUser {
        fun initUI()
        fun populateViews(string: String)
        fun setOffset(offsetPX: Int)
    }

    interface PresenterSingleUser {
        fun onViewsCreated() {}
        fun onDataPassed(string: String) {}
        fun setupOffset(offsetPX: Int) {}
        fun friendMap(currentID: String, lisResID: String)
    }
}