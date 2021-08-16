package com.aiconverse.voiceteller.services

import androidx.databinding.library.BuildConfig
import com.aiconverse.voiceteller.services.profile.ProfileService
import com.aiconverse.voiceteller.services.voiceprofile.VoiceProfileService
import com.aiconverse.voiceteller.services.wallet.WalletService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.Timeout
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


private const val BASE_URL = "https://voicetellerfunctions.azurewebsites.net/api/"

//private const val BASE_URL = "https://6458e335cd3d.ngrok.io/api/"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */

// add a http interceptor later

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi)
        .asLenient()
        .withNullSerialization())
    .baseUrl(BASE_URL)
    .client(
        OkHttpClient.Builder().also{
            if(BuildConfig.DEBUG){
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                it.addInterceptor(logging)
            }
//            it.callTimeout(3, TimeUnit.MINUTES)
//            it.connectTimeout(30, TimeUnit.SECONDS)
//            it.readTimeout(30, TimeUnit.SECONDS)
//            it.writeTimeout(30, TimeUnit.SECONDS)
        }.build()
    )
    .build()

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object ProfileApi {
    val profileService : ProfileService
    by lazy { retrofit.create(ProfileService::class.java) }
}

object WalletApi {
    val walletService: WalletService
    by lazy { retrofit.create(WalletService::class.java) }
}

object VoiceProfileApi{
    val voiceProfileService: VoiceProfileService
    by lazy { retrofit.create(VoiceProfileService::class.java) }
}