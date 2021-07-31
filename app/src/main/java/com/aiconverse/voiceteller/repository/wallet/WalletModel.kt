package com.aiconverse.voiceteller.repository.wallet

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WalletModel
    (
    var cardModel: MutableList<CardModel>?
    )