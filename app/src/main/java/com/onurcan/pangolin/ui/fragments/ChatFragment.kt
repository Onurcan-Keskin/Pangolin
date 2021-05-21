package com.onurcan.pangolin.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.onurcan.pangolin.ui.mvp.BaseFragment
import com.onurcan.pangolin.R
import com.onurcan.pangolin.databinding.FragmentChatBinding
import com.onurcan.pangolin.ui.contracts.IChat
import com.onurcan.pangolin.ui.presenters.ChatPresenter


class ChatFragment : BaseFragment(R.layout.fragment_chat), IChat.ViewChat {

    private lateinit var binding: FragmentChatBinding

    private lateinit var context: FragmentActivity

    private val presenter: ChatPresenter by lazy {
        ChatPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = requireActivity()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)
        presenter.onViewsCreate()
    }

    override fun initMakeFRecycler() {
        binding.makeFRecycler.layoutManager = GridLayoutManager(context, 3)
        presenter.setMakeFRecycler(context, context.supportFragmentManager, binding.makeFRecycler)
    }

}