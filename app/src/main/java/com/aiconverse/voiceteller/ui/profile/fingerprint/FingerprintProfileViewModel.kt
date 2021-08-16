package com.aiconverse.voiceteller.ui.profile.fingerprint

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.repository.profile.ProfileRepository

class FingerprintProfileViewModel : ViewModel() {

    fun setDiffStatus(id:String, status: String, applicationContext: Context){
        ProfileRepository.setDiffStatus(id, status, applicationContext)
    }

    fun getUserSharedPreferences(id: String, applicationContext: Context): ProfileModel?{
        return ProfileRepository.getUserFromSharedPreferences(id, applicationContext)
    }

    fun setUserSharedPreferences(profileModel: ProfileModel, applicationContext: Context){
        ProfileRepository.setUserSharedPreferences(profileModel, applicationContext)
    }
}