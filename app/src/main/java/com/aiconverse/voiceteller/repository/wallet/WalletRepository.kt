package com.aiconverse.voiceteller.repository.wallet

import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.services.WalletApi

object WalletRepository {
    private val walletApi = WalletApi.walletService

    suspend fun createCard(jwt: String, walletRequest: WalletRequest) : CardModel? {
        return walletApi.createCard("Bearer $jwt", walletRequest)
    }
}