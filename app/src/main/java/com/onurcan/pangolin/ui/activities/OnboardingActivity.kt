package com.onurcan.pangolin.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.onurcan.pangolin.databinding.ActivityOnboardingBinding
import com.onurcan.pangolin.ui.adapters.OnboardingPagerAdapter
import com.onurcan.pangolin.ui.fragments.Onboarding1Fragment
import com.onurcan.pangolin.ui.fragments.Onboarding2Fragment
import com.onurcan.pangolin.ui.fragments.Onboarding3Fragment

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupPager()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupPager(){
        val adapterL = OnboardingPagerAdapter(supportFragmentManager)
        adapterL.addFragment(Onboarding1Fragment(),"")
        adapterL.addFragment(Onboarding2Fragment(),"")
        adapterL.addFragment(Onboarding3Fragment(),"")

        binding.onboardPager.adapter = adapterL
        binding.eventsDots.setViewPager(binding.onboardPager)

    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}