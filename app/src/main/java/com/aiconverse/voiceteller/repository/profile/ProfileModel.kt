package com.aiconverse.voiceteller.repository.profile

import com.aiconverse.voiceteller.repository.wallet.WalletModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileModel
    (
    val id: String,
    val names: String,
    val authenticationType: String,
    val identityProvider: String,
    val createDate: String,
    val imageUrl: String,
    var walletModel: WalletModel? = null
)