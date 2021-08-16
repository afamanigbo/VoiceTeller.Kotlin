package com.aiconverse.voiceteller.services.voiceprofile

import com.aiconverse.voiceteller.repository.voiceprofile.VoiceEnrollmentModel
import com.aiconverse.voiceteller.repository.voiceprofile.VoiceProfileModel
import com.aiconverse.voiceteller.repository.voiceprofile.VoiceProfileRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface VoiceProfileService {
    @POST("voiceprofile")
    suspend fun createVoiceProfile(@Header("Authorization") jwt: String,
                           @Body voiceProfileRequest: VoiceProfileRequest
    ): VoiceProfileModel?

    @GET("voiceprofile")
    suspend fun getVoiceProfile(@Header("Authorization") jwt: String,
                                @Query("id") id: String,
                                @Query("profileId") profileId: String
    ): VoiceProfileModel?

    @DELETE("voiceprofile")
    suspend fun deleteVoiceProfile(@Header("Authorization") jwt: String,
                                   @Body voiceProfileRequest: VoiceProfileRequest
    ): Boolean

    @Multipart
    @POST("voiceenrollment")
    suspend fun enrollVoice(@Header("Authorization") jwt: String,
        @Part("id") id: RequestBody?,
        @Part("profileId") profileId: RequestBody?,
        @Part file: MultipartBody.Part
    ): VoiceEnrollmentModel
}