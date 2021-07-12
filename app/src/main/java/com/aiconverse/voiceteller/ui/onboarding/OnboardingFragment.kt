package com.aiconverse.voiceteller.ui.onboarding


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.aiconverse.voiceteller.R
import com.aiconverse.voiceteller.AuthActivity


class OnboardingFragment : Fragment() {
    var position : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("POSITION")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutId = when (position) {
            1 -> R.layout.fragment_onboarding1
            2 -> R.layout.fragment_onboarding2
            else -> R.layout.fragment_onboarding3
        }

        return layoutInflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cmdStart = view.findViewById<Button>(R.id.cmdStart)
        cmdStart.setOnClickListener(){

            val intent = Intent(activity, AuthActivity::class.java)
            startActivity(intent)

            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            activity?.finish()
        }

    }
}