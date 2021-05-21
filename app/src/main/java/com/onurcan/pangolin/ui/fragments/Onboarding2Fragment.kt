package com.onurcan.pangolin.ui.fragments

import android.os.Bundle
import android.view.View
import com.onurcan.pangolin.ui.mvp.BaseFragment
import com.onurcan.pangolin.R
import com.onurcan.pangolin.databinding.FragmentOnboarding2Binding

class Onboarding2Fragment : BaseFragment(R.layout.fragment_onboarding2) {

    private lateinit var binding : FragmentOnboarding2Binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOnboarding2Binding.bind(view)
    }
}