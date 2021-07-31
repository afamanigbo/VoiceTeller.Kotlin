package com.aiconverse.voiceteller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.aiconverse.voiceteller.ui.home.HomeFragment
import com.aiconverse.voiceteller.ui.others.OthersFragment
import com.aiconverse.voiceteller.ui.payment.PaymentFragment
import com.aiconverse.voiceteller.ui.profile.ProfileFragment
import com.aiconverse.voiceteller.ui.wallet.WalletFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val mHomeFragment = HomeFragment()
    private val mWalletFragment = WalletFragment()
    private val mPaymentFragment = PaymentFragment()
    private val mProfileFragment = ProfileFragment()
    private val mOthersFragment = OthersFragment()

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout_Container, mHomeFragment).commit()
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            return@setOnNavigationItemSelectedListener when (menuItem.itemId) {
                R.id.homeMenu -> {
                    loadFragment(mHomeFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.walletMenu -> {
                    loadFragment(mWalletFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.paymentMenu -> {
                    loadFragment(mPaymentFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.profileMenu -> {
                    loadFragment(mProfileFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.othersMenu -> {
                    loadFragment(mOthersFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout_Container, fragment)
        transaction.commit()
    }


}
