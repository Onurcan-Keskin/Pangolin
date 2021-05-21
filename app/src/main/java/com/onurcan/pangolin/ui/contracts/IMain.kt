package com.onurcan.pangolin.ui.contracts

class IMain {
    interface ViewMain{
        fun animateHamburgerToArrow()
        fun setupPager()
        fun populateNav()
        fun handleNavItemClicks()
        fun initOneSignal()
    }

    interface PresenterMain{
        fun onViewsCreate()
    }
}