package com.aiconverse.voiceteller.repository.voiceprofile

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoiceProfileModel
    (
    val profileId: String,
    val locale: String,
    val enrollmentStatus: String,
    val createdDateTime: String? = "",
    val lastUpdatedDateTime: String? = "",
    val enrollmentsCount: String,
    val enrollmentsLength: String,
    val enrollmentsSpeechLength: String,
    val remainingEnrollmentsSpeechLength: String,
    val modelVersion: String? = ""
)
