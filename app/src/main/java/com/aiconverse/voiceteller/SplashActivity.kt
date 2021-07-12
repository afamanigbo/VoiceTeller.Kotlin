package com.aiconverse.voiceteller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.ImageView

class SplashActivity : AppCompatActivity() {

    private lateinit var txtSplash: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        txtSplash = findViewById(R.id.txtSplash)

        txtSplash.alpha = 0F
        txtSplash.animate().setDuration(3000).alpha(1f).withEndAction{
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }



    }
}