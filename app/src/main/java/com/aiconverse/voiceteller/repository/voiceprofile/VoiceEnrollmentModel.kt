package com.aiconverse.voiceteller.repository.voiceprofile

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoiceEnrollmentModel(
    val audioLength: String? = "",
    val audioSpeechLength: String? = "",
    val enrollmentStatus: String? = "",
    val enrollmentsCount: String? = "",
    val enrollmentsLength: String? = "",
    val enrollmentsSpeechLength: String? = "",
    val passPhrase: String? = "",
    val profileId: String? = "",
    val remainingEnrollmentsCount: String? = ""
)