package com.aiconverse.voiceteller.ui.profile.voice

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.repository.profile.ProfileRepository
import com.aiconverse.voiceteller.repository.voiceprofile.*
import com.aiconverse.voiceteller.repository.wallet.CardModel
import com.aiconverse.voiceteller.repository.wallet.WalletRepository
import com.aiconverse.voiceteller.repository.wallet.WalletRequest
import kotlinx.coroutines.launch
import timber.log.Timber

class VoiceProfileViewModel : ViewModel() {

    private val _apiVoiceProfileModel = MutableLiveData<VoiceProfileModel?>()
    val apiVoiceProfileModel: LiveData<VoiceProfileModel?>
        get() = _apiVoiceProfileModel


    private val _apiVoiceEnrollmentModel = MutableLiveData<VoiceEnrollmentModel?>()
    val apiVoiceEnrollmentModel: LiveData<VoiceEnrollmentModel?>
        get() = _apiVoiceEnrollmentModel

    fun enrollVoice(jwt: String,
                    id: String,
                    profileId: String,
                    fileName: String){
        viewModelScope.launch {
            try{
                _apiVoiceEnrollmentModel.value =
                    VoiceProfileRepository.enrollVoice(jwt, id, profileId, fileName)
            }
            catch(e: Exception){
                Timber.e("M: Error enrolling voice audio ${e.stackTraceToString()}")
                _apiVoiceEnrollmentModel.value = null
            }
        }
    }

    fun createProfile(jwt: String, voiceProfileRequest: VoiceProfileRequest){
        viewModelScope.launch {
            try{
                _apiVoiceProfileModel.value =
                    VoiceProfileRepository.createVoiceProfile(jwt, voiceProfileRequest)
            }
            catch (e: Exception){
                Timber.e("M: Error creating voice profile ${e.stackTraceToString()}")
                _apiVoiceProfileModel.value = null
            }
        }
    }

    fun getProfile(jwt: String, voiceProfileRequest: VoiceProfileRequest){
        viewModelScope.launch {
            try{
                _apiVoiceProfileModel.value =
                    VoiceProfileRepository.getVoiceProfile(jwt, voiceProfileRequest)
            }
            catch (e: Exception){
                Timber.d("M: Error getting voice profile ${e.toString()}")
                _apiVoiceProfileModel.value = null
            }
        }
    }

    fun setDiffStatus(id:String, status: String, applicationContext: Context){
        ProfileRepository.setDiffStatus(id, status, applicationContext)
    }

    fun getUserSharedPreferences(id: String, applicationContext: Context): ProfileModel?{
        return ProfileRepository.getUserFromSharedPreferences(id, applicationContext)
    }

    fun setUserSharedPreferences(profileModel: ProfileModel, applicationContext: Context){
        ProfileRepository.setUserSharedPreferences(profileModel, applicationContext)
    }

    fun updateVoiceProfile(id: String, applicationContext: Context, voiceProfileModel: VoiceProfileModel) {
        ProfileRepository.updateVoiceProfile(id, applicationContext, voiceProfileModel)
    }

    fun updateVoiceEnrollment(id: String, applicationContext: Context, voiceEnrollmentModel: VoiceEnrollmentModel) {
        Timber.d("M: Updating enrollment")
        ProfileRepository.updateVoiceEnrollment(id, applicationContext, voiceEnrollmentModel)
    }
}