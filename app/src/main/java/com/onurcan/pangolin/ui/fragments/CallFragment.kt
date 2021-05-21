package com.onurcan.pangolin.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.onurcan.pangolin.ui.mvp.BaseFragment
import com.onurcan.pangolin.R
import com.onurcan.pangolin.databinding.FragmentCallBinding


class CallFragment : BaseFragment(R.layout.fragment_call) {

    private lateinit var binding: FragmentCallBinding

    private lateinit var context: FragmentActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        context = activity!!
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCallBinding.bind(view)
    }

}