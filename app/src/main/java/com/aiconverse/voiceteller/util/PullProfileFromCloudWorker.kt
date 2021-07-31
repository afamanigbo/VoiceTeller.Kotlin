package com.aiconverse.voiceteller.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aiconverse.voiceteller.repository.profile.ProfileRepository
import timber.log.Timber

class PullProfileFromCloudWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "PULL_PROFILE_FROM_CLOUD"
    }

    override suspend fun doWork(): Result {

        try {
            val id = inputData.getString("id")
            val jwt = inputData.getString("jwt")

            val response = ProfileRepository.getUserApi(jwt!!, id!!)

            ProfileRepository.setUserCloudStatus(
                id,
                response.code().toString(),
                applicationContext)

            if(response.isSuccessful){

                val profileModel = response.body()

                ProfileRepository.setUserSharedPreferences(profileModel!!, applicationContext)

                Timber.d("M: Success getting profile from cloud: %s", response.code())

                return Result.success()

            }
            else{

                Timber.d("M: Failed getting profile from cloud: %s", response.code())

                return Result.failure()

            }
        } catch (e: Exception) {

            Timber.d("Exception getting profile from cloud: %s", e.toString())
            Timber.e(e)

            return Result.failure()
        }

    }

}


