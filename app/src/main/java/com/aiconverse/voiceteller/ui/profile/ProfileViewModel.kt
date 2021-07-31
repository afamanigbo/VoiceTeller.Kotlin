package com.aiconverse.voiceteller.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.repository.profile.ProfileRepository

class ProfileViewModel : ViewModel() {

    fun getUserSharedPreferences(id: String, applicationContext: Context): ProfileModel?{
        return ProfileRepository.getUserFromSharedPreferences(id, applicationContext)
    }

    fun createUserSharedPreferences(profileModel: ProfileModel, applicationContext: Context){
        ProfileRepository.setUserSharedPreferences(profileModel, applicationContext)
    }

}