package com.aiconverse.voiceteller.util

import com.aiconverse.voiceteller.R
import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.repository.wallet.WalletModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.*


object UtilManager {

    fun minorDenomination(amount: Long): Long {
        return amount * 100
    }

    fun getUniqueReference(): String{
        return UUID.randomUUID().toString()
    }

    fun deSerializeJsonProfileModel(json: String): ProfileModel? {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(ProfileModel::class.java)

        Timber.d("M: Moshi input json string: $json")
        return jsonAdapter.fromJson(json)
    }

    fun serializeJsonProfileModel(profileModel: ProfileModel): String? {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(ProfileModel::class.java)

        val retval = jsonAdapter.toJson(profileModel)

        Timber.d("M: Moshi output json string: $retval")

        return retval
    }

    fun deSerializeJsonWalletModel(json: String): WalletModel? {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(WalletModel::class.java)

        return jsonAdapter.fromJson(json)
    }

    fun serializeJsonWalletModel(walletModel: WalletModel): String? {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(WalletModel::class.java)

        return jsonAdapter.toJson(walletModel)
    }

    fun cardToImage(cardType: String): Int{

        return when(cardType.uppercase()){
            "MASTERCARD" -> R.drawable.ic_mastercard_symbol
            "VISA" -> R.drawable.visalogo
            "VERVE" -> R.drawable.isw_ic_card_verve
            "AMERICANEXPRESS" -> R.drawable.isw_ic_card_american_express
            "DINERSCLUB" -> R.drawable.isw_ic_card_diners_club
            "DISCOVER" -> R.drawable.isw_ic_card_discover
            "MAESTRO" -> R.drawable.isw_ic_card_maestro
            "UNIONPAY" -> R.drawable.isw_ic_card_unionpay
            else -> R.drawable.isw_ic_card_empty
        }
    }
}