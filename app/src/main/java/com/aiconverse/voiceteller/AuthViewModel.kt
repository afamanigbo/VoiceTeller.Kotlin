package com.aiconverse.voiceteller

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import androidx.work.*
import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.repository.profile.ProfileRepository
import com.aiconverse.voiceteller.util.PullProfileFromCloudWorker
import com.aiconverse.voiceteller.util.PushProfileToCloudWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AuthViewModel(application: Application): AndroidViewModel(application) {


    fun createUserWorker(jwt: String, profileModel: ProfileModel){



        val inputData = Data.Builder()
            .putString("jwt", jwt)
            .putString("id", profileModel.id)
            .build()

        val work = OneTimeWorkRequestBuilder<PushProfileToCloudWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(getApplication())
            .beginUniqueWork(PushProfileToCloudWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
                work)
                .enqueue()

    }

    fun getUserWorker(jwt: String, id: String){
        val inputData = Data.Builder()
            .putString("jwt", jwt)
            .putString("id", id)
            .build()

        val work = OneTimeWorkRequestBuilder<PullProfileFromCloudWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(getApplication())
            .beginUniqueWork(PullProfileFromCloudWorker.WORK_NAME,
                ExistingWorkPolicy.KEEP,
                work)
                .enqueue()
    }

    fun getUserSharedPreferences(id: String, applicationContext: Context): ProfileModel?{
        return ProfileRepository.getUserFromSharedPreferences(id, applicationContext)
    }

    fun createUserSharedPreferences(profileModel: ProfileModel, applicationContext: Context){
        ProfileRepository.setUserSharedPreferences(profileModel, applicationContext)
    }

    fun getUserCloudStatus(id: String, applicationContext: Context) : String?{
        return ProfileRepository.getUserCloudStatus(id, applicationContext)
    }



}