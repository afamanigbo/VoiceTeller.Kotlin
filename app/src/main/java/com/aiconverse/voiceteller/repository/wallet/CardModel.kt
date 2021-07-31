package com.aiconverse.voiceteller.repository.wallet

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CardModel
    (
    val expiry: String,
    val panLast4Digits: String,
    val cardType: String,
    val token: String,
    val txnref: String
)