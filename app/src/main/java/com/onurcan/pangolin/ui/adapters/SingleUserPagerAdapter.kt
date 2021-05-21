package com.onurcan.pangolin.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.onurcan.pangolin.ui.activities.MainActivity
import com.onurcan.pangolin.ui.fragments.DocsFragment
import com.onurcan.pangolin.ui.fragments.LinksFragment
import com.onurcan.pangolin.ui.fragments.MediaFragment

class SingleUserPagerAdapter constructor(fragmentManager: FragmentManager) :
    FragmentStateAdapter(fragmentManager, MainActivity().lifecycle) {

    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                fragment = MediaFragment.newInstance("", mFragmentTitleList[position])
            }
            1 -> {
                fragment = DocsFragment.newInstance("", mFragmentTitleList[position])
            }
            2 -> {
                fragment = LinksFragment.newInstance("", mFragmentTitleList[position])
            }
        }
        return fragment!!
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }
}