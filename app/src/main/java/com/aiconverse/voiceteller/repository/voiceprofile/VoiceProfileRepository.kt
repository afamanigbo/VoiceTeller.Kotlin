package com.aiconverse.voiceteller.repository.voiceprofile

import com.aiconverse.voiceteller.services.VoiceProfileApi
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


object VoiceProfileRepository {
    private val voiceProfileApi = VoiceProfileApi.voiceProfileService

    suspend fun createVoiceProfile
                (jwt: String,
                voiceProfileRequest: VoiceProfileRequest) : VoiceProfileModel? {
        return voiceProfileApi.createVoiceProfile("Bearer $jwt", voiceProfileRequest)
    }

    suspend fun getVoiceProfile
                (jwt: String,
                 voiceProfileRequest: VoiceProfileRequest) : VoiceProfileModel? {
        return voiceProfileApi.getVoiceProfile("Bearer $jwt",
                                                voiceProfileRequest.id,
                                                voiceProfileRequest.profileId)
    }

    suspend fun deleteVoiceProfile
                (jwt: String,
                 voiceProfileRequest: VoiceProfileRequest) : Boolean {
        return voiceProfileApi.deleteVoiceProfile("Bearer $jwt", voiceProfileRequest)
    }

    suspend fun enrollVoice
                (jwt: String,
                 id: String,
                 profileId: String,
                 fileName: String): VoiceEnrollmentModel
    {

        val file = File(fileName)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        val body = createFormData("file", file.name, requestFile)

        val id = id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val profileId = profileId.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return voiceProfileApi.enrollVoice("Bearer $jwt", id, profileId, body)
    }
}