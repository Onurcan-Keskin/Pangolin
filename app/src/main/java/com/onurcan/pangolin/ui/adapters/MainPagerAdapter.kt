package com.onurcan.pangolin.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.onurcan.pangolin.ui.activities.MainActivity
import com.onurcan.pangolin.ui.fragments.CallFragment
import com.onurcan.pangolin.ui.fragments.CameraFragment
import com.onurcan.pangolin.ui.fragments.ChatFragment

class MainPagerAdapter constructor(fragmentManager: FragmentManager) :
    FragmentStateAdapter(fragmentManager, MainActivity().lifecycle) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()


    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                fragment = CameraFragment()
            }
            1 -> {
                fragment = ChatFragment()
            }
            2 -> {
                fragment = CallFragment()
            }
        }

        return fragment!!
    }


}