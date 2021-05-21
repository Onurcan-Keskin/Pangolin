package com.onurcan.pangolin.ui.presenters

import com.onurcan.pangolin.ui.contracts.IMain

class MainPresenter constructor(private val contract:IMain.ViewMain): IMain.PresenterMain {

    override fun onViewsCreate(){
        contract.setupPager()
        contract.populateNav()
        contract.initOneSignal()
    }
}