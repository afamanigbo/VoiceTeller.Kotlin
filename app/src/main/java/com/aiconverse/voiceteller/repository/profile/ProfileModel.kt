package com.aiconverse.voiceteller.repository.profile

import com.aiconverse.voiceteller.repository.voiceprofile.VoiceEnrollmentModel
import com.aiconverse.voiceteller.repository.voiceprofile.VoiceProfileModel
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
    var walletModel: WalletModel? = null,
    var fingerprintConfirmation: Boolean = false,
    var voiceConfirmation: Boolean = false,
    var voiceProfileModel: VoiceProfileModel? = null,
    var voiceEnrollmentModel: VoiceEnrollmentModel? = null
)