package com.aiconverse.voiceteller.ui.profile.fingerprint

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.databinding.DataBindingUtil
import com.aiconverse.voiceteller.R
import com.aiconverse.voiceteller.databinding.FingerprintProfileFragmentBinding
import com.aiconverse.voiceteller.databinding.ProfileFragmentBinding
import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.ui.profile.ProfileViewModel
import com.aiconverse.voiceteller.ui.wallet.WalletViewModel
import com.google.firebase.auth.FirebaseAuth

class FingerprintProfileFragment : Fragment() {

    companion object {
        fun newInstance() = FingerprintProfileFragment()
    }

    private val viewModel: FingerprintProfileViewModel by lazy {
        ViewModelProvider(this).get(FingerprintProfileViewModel::class.java)
    }

    private lateinit var biometricManager: BiometricManager

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val user = firebaseAuth.currentUser

    private lateinit var profileModel: ProfileModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FingerprintProfileFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fingerprint_profile_fragment, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // load profile
        profileModel = viewModel.getUserSharedPreferences(user?.email!!,requireContext())!!

        biometricManager = BiometricManager.from(requireContext())

        // set initial form status
        binding.cmdConfirm.isChecked = profileModel.fingerprintConfirmation

        // check if biometric is enabled
        val errorMessage = verfiyBiometricExistence()

        if(errorMessage != ""){
            binding.txtStatus.text = errorMessage
            binding.cmdConfirm.isEnabled = false
        }
        else{

            binding.txtStatus.text =
                setFingerprintStatus(profileModel.fingerprintConfirmation)

            binding.cmdConfirm.setOnCheckedChangeListener { buttonView, isChecked ->

                updateProfile(isChecked)

                binding.txtStatus.text =
                    setFingerprintStatus(isChecked)
            }
        }

        return binding.root

    }

    private fun updateProfile(isChecked: Boolean){
        profileModel.fingerprintConfirmation = isChecked

        if(isChecked){
            profileModel.voiceConfirmation = false
        }

        viewModel.setUserSharedPreferences(profileModel, requireContext())
        viewModel.setDiffStatus(profileModel.id, "PUSH", requireContext())

    }

    private fun setFingerprintStatus(isChecked:Boolean): String{
        return if(isChecked) "Fingerprint confirmation enabled."
        else{
            "Fingerprint confirmation disabled."
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun verfiyBiometricExistence(): String {
        var errorMessage = ""

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                errorMessage = ""

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                errorMessage = "Fingerprint reader not available."

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                errorMessage = "Fingerprint reader not available at this time."

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                errorMessage =
                    "No Fingerprint has been enrolled."
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                errorMessage = "Fingerprint reader unavailable.\nSecurity update required."
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                errorMessage = "Fingerprint reader not supported."
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                errorMessage = "Fingerprint reader status unknown."
            }
        }

        return errorMessage
    }

}



