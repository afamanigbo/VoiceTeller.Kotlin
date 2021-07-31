package com.aiconverse.voiceteller.services.profile

import com.aiconverse.voiceteller.repository.profile.ProfileModel
import retrofit2.Response
import retrofit2.http.*

interface ProfileService {

    @POST("profile")
    suspend fun createUser(@Header("Authorization") jwt: String,
                           @Body profileModel: ProfileModel): Response<String>

    @GET("profile")
    suspend fun getUser(@Header("Authorization") jwt: String,
                        @Query("id") id: String): Response<ProfileModel>
}