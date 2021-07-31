package com.aiconverse.voiceteller.services.wallet

import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.repository.wallet.CardModel
import com.aiconverse.voiceteller.repository.wallet.WalletModel
import com.aiconverse.voiceteller.repository.wallet.WalletRequest
import retrofit2.http.*

interface WalletService {
    @POST("wallet")
    suspend fun createCard(@Header("Authorization") jwt: String,
                           @Body walletRequest: WalletRequest): CardModel?
}