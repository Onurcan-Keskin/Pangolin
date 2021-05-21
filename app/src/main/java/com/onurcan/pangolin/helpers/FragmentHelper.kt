package com.onurcan.pangolin.helpers

import android.app.Activity
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.onurcan.pangolin.R
import com.onurcan.pangolin.ui.fragments.SingleUserFragment

object FragmentHelper {

    fun popBackStack(context:FragmentActivity){
        context.findViewById<FrameLayout>(R.id.main_frame).visibility= View.GONE
        context.findViewById<ViewPager2>(R.id.main_pager).visibility= View.VISIBLE
        context.findViewById<AppBarLayout>(R.id.appBarLayout).visibility= View.VISIBLE
        context.supportFragmentManager.popBackStack()
    }

    fun addToBackStack(context:Activity, fragment: Fragment, fragmentManager:FragmentManager){
        context.findViewById<FrameLayout>(R.id.main_frame).visibility= View.VISIBLE
        context.findViewById<ViewPager2>(R.id.main_pager).visibility= View.GONE
        context.findViewById<AppBarLayout>(R.id.appBarLayout).visibility= View.GONE
        context.findViewById<DrawerLayout>(R.id.main_drawer).closeDrawer(GravityCompat.START)
        fragmentManager.beginTransaction().run {
            return@run setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_left
            )
        }.add(R.id.main_frame, fragment,fragmentManager.javaClass.simpleName)
            .addToBackStack(fragmentManager.javaClass.name)
            .commit()
    }

    fun startSingleUserFragmentBundle(userId:String, fragmentManager: FragmentManager, context: FragmentActivity){
        fragmentManager.setFragmentResult(
            "requestKey",
            bundleOf("bundle" to userId)
        )
        addToBackStack(context,SingleUserFragment(),fragmentManager)
    }

    fun onKeyBack(T:Fragment, context: FragmentActivity){
        T.view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == KeyEvent.KEYCODE_BACK) {
                    popBackStack(context)
                    return true
                }
                return false
            }
        })
    }
}