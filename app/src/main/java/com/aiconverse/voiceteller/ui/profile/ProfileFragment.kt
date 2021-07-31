package com.aiconverse.voiceteller.ui.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.aiconverse.voiceteller.R
import com.aiconverse.voiceteller.databinding.ProfileFragmentBinding
import com.aiconverse.voiceteller.ui.wallet.WalletViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private val user = FirebaseAuth.getInstance().currentUser

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

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}