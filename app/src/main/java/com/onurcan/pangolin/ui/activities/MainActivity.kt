package com.onurcan.pangolin.ui.activities

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.view.GravityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.pangolin.R
import com.onurcan.pangolin.databinding.ActivityMainBinding
import com.onurcan.pangolin.helpers.Constants
import com.onurcan.pangolin.helpers.FragmentHelper
import com.onurcan.pangolin.models.FirebaseDBObject
import com.onurcan.pangolin.ui.adapters.MainPagerAdapter
import com.onurcan.pangolin.ui.contracts.IMain
import com.onurcan.pangolin.ui.fragments.*
import com.onurcan.pangolin.ui.presenters.MainPresenter
import com.onurcan.pangolin.utils.expandView
import com.squareup.picasso.Picasso
import nl.joery.animatedbottombar.AnimatedBottomBar
import push.HmsGmsMessagingService

class MainActivity : AppCompatActivity(), IMain.ViewMain {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mDrawerToggle: ActionBarDrawerToggle

    private val presenter: MainPresenter by lazy {
        MainPresenter(this)
    }

    private val hmsGmsTokenService: HmsGmsMessagingService by lazy {
        HmsGmsMessagingService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        presenter.onViewsCreate()
        mDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.mainDrawer,
            R.string.openDrawer,
            R.string.closeDrawer
        )
        mDrawerToggle.syncState()
        binding.mainDrawer.addDrawerListener(mDrawerToggle)

        binding.mainDrawerOpener.setOnClickListener {
            binding.mainDrawer.openDrawer(GravityCompat.START)
        }

        binding.mainBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newIndex) {
                    0 -> {
                        binding.appBarLayout.visibility = View.GONE
                    }
                    1 -> {
                        binding.appBarLayout.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.appBarLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
                super.onTabReselected(index, tab)
                when (index) {
                    0 -> {
                        binding.appBarLayout.visibility = View.GONE
                    }
                    1 -> {
                        binding.appBarLayout.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.appBarLayout.visibility = View.VISIBLE
                    }
                }
            }
        })

        binding.mainSearchButton.setOnClickListener {
            animateHamburgerToArrow()
            binding.mainBar.expandView()
            binding.mainSearchLyt.expandView()
        }

        handleNavItemClicks()
    }

    override fun initOneSignal(){
        hmsGmsTokenService.getToken()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun populateNav() {
        Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                val photoUrl = snapshot.child("photoUrl").value.toString()
                val email = snapshot.child("email").value.toString()

                binding.mainNavInc.navProfName.text = name
                binding.mainNavInc.navProfEmail.text = email


                Picasso.get().load(photoUrl).centerCrop().fit().into(binding.mainNavInc.navProfImg)
            }

            override fun onCancelled(error: DatabaseError) {
                showLogError(this.javaClass.simpleName, error.toString())
            }
        })
    }

    override fun handleNavItemClicks() {
        val fragmentManager = supportFragmentManager
        /* Contacts */
        /* Pending */
        binding.mainNavInc.navPending.setOnClickListener {
            FragmentHelper.addToBackStack(this,PendingRequestFragment(),fragmentManager)
        }
        /* About */
        /* Settings */
        binding.mainNavInc.navSettings.setOnClickListener {
            FragmentHelper.addToBackStack(this, SettingsFragment(), fragmentManager)
        }
    }

    override fun animateHamburgerToArrow() {
        val drawable = DrawerArrowDrawable(this)
        binding.mainDrawerOpener.setImageDrawable(drawable)
        val anim = ValueAnimator.ofFloat(0f, 1f)
        anim.addUpdateListener {
            drawable.progress = (it.animatedValue as Float?)!!
        }
        anim.interpolator = DecelerateInterpolator()
        anim.duration = 500
        anim.start()

        if (binding.mainSearchLyt.visibility == View.VISIBLE) {
            anim.reverse()
        }
    }

    override fun setupPager() {
        val adapterL = MainPagerAdapter(supportFragmentManager)
        adapterL.addFragment(CameraFragment(), getString(R.string.camera))
        adapterL.addFragment(ChatFragment(), getString(R.string.chat))
        adapterL.addFragment(CallFragment(), getString(R.string.call))

        binding.mainPager.adapter = adapterL
        binding.mainPager.currentItem = 1
        binding.mainBar.setupWithViewPager2(binding.mainPager)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showLogInfo(this.javaClass.simpleName, "KeyEvent.KEYCODE_BACK")
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        FirebaseDBObject.onConnect(Constants.fOnlineDBRef)
    }

    override fun onPause() {
        super.onPause()
        FirebaseDBObject.onDisconnect(Constants.fOnlineDBRef)
    }

    override fun onStop() {
        super.onStop()
        FirebaseDBObject.onDisconnect(Constants.fOnlineDBRef)
    }
}