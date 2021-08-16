package com.aiconverse.voiceteller.ui.profile

import android.app.AlertDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.aiconverse.voiceteller.AuthActivity
import com.aiconverse.voiceteller.R
import com.aiconverse.voiceteller.databinding.ProfileFragmentBinding
import com.aiconverse.voiceteller.ui.profile.fingerprint.FingerprintProfileFragment
import com.aiconverse.voiceteller.ui.profile.voice.VoiceProfileFragment
import com.aiconverse.voiceteller.ui.wallet.WalletViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val user = firebaseAuth.currentUser

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: ProfileFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.profile_fragment, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        val profileModel = viewModel.getUserSharedPreferences(user?.email!!, requireContext())

        profileModel?.let {
            binding.txtNames.text = it.names
            binding.txtUserName.text = it.id
            Glide.with(this)
                .load(it.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .into(binding.imageProfile)
        }

        binding.txtSignout.setOnClickListener{
            var builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirm Action.")
            builder.setMessage("Sure to sign out?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                // sign out
                firebaseAuth.signOut()
                dialog.cancel()

                val intent = Intent(activity, AuthActivity::class.java)
                startActivity(intent)

                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                activity?.finish()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

            var alert = builder.create()
            alert.show()
        }

        // handle menu items

        binding.cmdFingerprint.setOnClickListener{

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.frameLayout_Container, FingerprintProfileFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        binding.cmdVoice.setOnClickListener{

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.frameLayout_Container, VoiceProfileFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}