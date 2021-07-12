package com.aiconverse.voiceteller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.aiconverse.voiceteller.ui.onboarding.OnboardingAdapter
import com.cuberto.liquid_swipe.LiquidPager

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val pager = findViewById<LiquidPager>(R.id.pager)
        pager.adapter = OnboardingAdapter(supportFragmentManager)

    }
}
