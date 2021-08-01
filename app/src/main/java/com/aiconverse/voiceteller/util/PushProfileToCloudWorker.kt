package com.aiconverse.voiceteller.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aiconverse.voiceteller.repository.profile.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Thread.sleep

class PushProfileToCloudWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "PUSH_PROFILE_TO_CLOUD"
    }

    override suspend fun doWork(): Result {

        try {

            val id = inputData.getString("id")
            val jwt = inputData.getString("jwt")

            val updateStatus =
                ProfileRepository.getDiffStatus(id!!, applicationContext)

            Timber.d("M: DIFF Status %s", updateStatus)

            if (updateStatus != "PUSH"){
                return Result.success()
            }

            val profileModel =
                ProfileRepository.getUserFromSharedPreferences(id!!, applicationContext)

            if ((profileModel != null) && (jwt != null)) {

                val response = ProfileRepository.createUserApi(jwt, profileModel)

                ProfileRepository.setUserCloudStatus(
                    id,
                    response.code().toString(),
                    applicationContext)

                if(response.isSuccessful) {
                    Timber.d("M: Profile Sync Successful %s", response.code())

                    ProfileRepository.setDiffStatus(id!!, "STAY", applicationContext)
                    return Result.success()
                }
                else{
                    Timber.d("M: Profile Sync Failed %s", response.code())
                    return Result.failure()
                }

            } else {

                Timber.d("M: Profile Sync Failed with Null Values")
                return Result.failure()

            }

        } catch (e: Exception) {
            Timber.d("M: Profile Sync Exception %s", e.toString())
            Timber.e(e)
            return Result.failure()
        }

    }


}