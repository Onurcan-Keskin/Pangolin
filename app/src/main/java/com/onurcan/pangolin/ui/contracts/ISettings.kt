package com.onurcan.pangolin.ui.contracts

class ISettings {
    interface ViewSettings {
        fun initUI()
        fun populateView()
        fun setOffset(offsetPX: Int)
        fun initNameDialog(type:Int)
        fun initBioDialog(type:Int)
    }

    interface PresenterSettings{
        fun onViewsCreated()
    }
}