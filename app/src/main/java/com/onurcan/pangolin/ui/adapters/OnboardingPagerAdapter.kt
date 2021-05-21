package com.onurcan.pangolin.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.onurcan.pangolin.ui.fragments.Onboarding1Fragment
import com.onurcan.pangolin.ui.fragments.Onboarding2Fragment
import com.onurcan.pangolin.ui.fragments.Onboarding3Fragment

class OnboardingPagerAdapter constructor(fragmentManager: FragmentManager) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position){
            0->{
                fragment= Onboarding1Fragment()
            }
            1->{
                fragment= Onboarding2Fragment()
            }
            2->{
                fragment= Onboarding3Fragment()
            }
        }
        return  fragment!!
    }

    fun addFragment(fragment: Fragment,title:String){
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }
}