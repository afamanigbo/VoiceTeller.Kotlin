package com.aiconverse.voiceteller.repository.profile

import android.content.Context
import com.aiconverse.voiceteller.repository.voiceprofile.VoiceEnrollmentModel
import com.aiconverse.voiceteller.repository.voiceprofile.VoiceProfileModel
import com.aiconverse.voiceteller.repository.wallet.CardModel
import com.aiconverse.voiceteller.repository.wallet.WalletModel
import com.aiconverse.voiceteller.services.ProfileApi
import com.aiconverse.voiceteller.util.PreferencesManager
import com.aiconverse.voiceteller.util.UtilManager
import retrofit2.Response
import timber.log.Timber

object ProfileRepository {

    private lateinit var preferencesManager: PreferencesManager
    private val profileApi = ProfileApi.profileService

    suspend fun createUserApi(jwt: String, profileModel: ProfileModel): Response<String> {
        return profileApi.createUser("Bearer $jwt", profileModel)
    }

    suspend fun getUserApi(jwt: String, id: String): Response<ProfileModel> {
        return profileApi.getUser("Bearer $jwt", id)
    }

    fun getUserCloudStatus(id: String, applicationContext: Context): String? {
        val statusId = id + "_CLOUD_STATUS"
        return getItem(statusId, applicationContext)
    }

    fun getDiffStatus(id: String, applicationContext:Context): String?{
        val statusId = id + "_DIFF_STATUS"
        return getItem(statusId, applicationContext)
    }

    fun setDiffStatus(id: String, status: String,  applicationContext: Context) {
        val statusId = id + "_DIFF_STATUS"
        setItem(statusId, status, applicationContext)
    }

    fun setUserCloudStatus(id: String, status: String,  applicationContext: Context) {
        val statusId = id + "_CLOUD_STATUS"
        setItem(statusId, status, applicationContext)
    }

    private fun getItem(id: String, applicationContext: Context): String?{
        preferencesManager = PreferencesManager.getInstance(applicationContext)
        return preferencesManager.getItem(id)
    }

    private fun setItem(id: String, value:String, applicationContext: Context){
        preferencesManager = PreferencesManager.getInstance(applicationContext)
        preferencesManager.setItem(id, value)
    }

    fun getUserFromSharedPreferences(id: String, applicationContext: Context): ProfileModel? {

        preferencesManager = PreferencesManager.getInstance(applicationContext)

        val profileString = preferencesManager.getItem(id)

        Timber.d("M: Get preferences: Profile String %s", profileString)

        return if(profileString != ""){
            UtilManager.deSerializeJsonProfileModel(profileString!!)
        }
        else{
            null
        }

    }

    fun setUserSharedPreferences(profileModel: ProfileModel, applicationContext: Context){
        preferencesManager = PreferencesManager.getInstance(applicationContext)

        val profileString = UtilManager.serializeJsonProfileModel(profileModel)

        Timber.d("M: Set preferences: Profile String %s", profileString)

        preferencesManager
            .setItem(profileModel.id, profileString!!)

    }

    fun addCardToWallet(id: String, applicationContext: Context, cardModel: CardModel){
        var profileModel = getUserFromSharedPreferences(id, applicationContext)

        var listCardModel = mutableListOf<CardModel>()
        listCardModel.add(cardModel)

        if(profileModel != null){
            if(profileModel.walletModel == null){
                profileModel.walletModel = WalletModel(listCardModel)
            }
            else{
                profileModel.walletModel?.cardModel?.add(cardModel)
            }

            setUserSharedPreferences(profileModel, applicationContext)
        }
    }

    fun updateVoiceProfile(id: String, applicationContext: Context, voiceProfileModel: VoiceProfileModel){
        var profileModel = getUserFromSharedPreferences(id, applicationContext)

        if(profileModel != null){
            profileModel.voiceProfileModel = voiceProfileModel
            setUserSharedPreferences(profileModel, applicationContext)
        }
    }

    fun updateVoiceEnrollment(id: String, applicationContext: Context, voiceEnrollmentModel: VoiceEnrollmentModel){
        var profileModel = getUserFromSharedPreferences(id, applicationContext)

        if(profileModel != null){
            profileModel.voiceEnrollmentModel = voiceEnrollmentModel
            setUserSharedPreferences(profileModel, applicationContext)
        }
    }

    fun getCardsFromWallet(id: String, applicationContext: Context): MutableList<CardModel>? {
        var profileModel = getUserFromSharedPreferences(id, applicationContext)

        return profileModel?.walletModel?.cardModel
    }


}