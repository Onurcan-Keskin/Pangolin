package com.onurcan.pangolin.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.onurcan.pangolin.R
import com.onurcan.pangolin.databinding.FragmentPendingRequestBinding
import com.onurcan.pangolin.helpers.FragmentHelper
import com.onurcan.pangolin.helpers.SwipeDismissActions
import com.onurcan.pangolin.ui.contracts.IPendingRequest
import com.onurcan.pangolin.ui.mvp.BaseFragment
import com.onurcan.pangolin.ui.presenters.PendingRequestPresenter

class PendingRequestFragment : BaseFragment(R.layout.fragment_pending_request),
    IPendingRequest.ViewPendingRequest {

    private lateinit var binding: FragmentPendingRequestBinding
    private lateinit var context: FragmentActivity

    private val presenter: PendingRequestPresenter by lazy {
        PendingRequestPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPendingRequestBinding.bind(view)
        FragmentHelper.onKeyBack(this, context)

        presenter.onViewCreated()
    }

    override fun initUI() {
        binding.backArrow.setOnClickListener {
            FragmentHelper.popBackStack(context)
        }
        SwipeDismissActions.fragmentDismiss(binding.fragMaterialRoot, context)

        binding.pendingRequestRecycler.layoutManager = LinearLayoutManager(context)
        presenter.setPendingRecycler(
            context,
            context.supportFragmentManager,
            binding.pendingRequestRecycler
        )
    }


}