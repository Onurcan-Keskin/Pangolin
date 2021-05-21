package com.onurcan.pangolin.ui.fragments

import android.os.Bundle
import android.view.View
import com.onurcan.pangolin.ui.mvp.BaseFragment
import com.onurcan.pangolin.R
import com.onurcan.pangolin.databinding.FragmentOnboarding1Binding

class Onboarding1Fragment : BaseFragment(R.layout.fragment_onboarding1) {

    private lateinit var binding: FragmentOnboarding1Binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOnboarding1Binding.bind(view)
    }
}